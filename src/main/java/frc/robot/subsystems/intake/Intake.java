package frc.robot.subsystems.intake;

import static frc.robot.Constants.Intake.INTAKE_BEAM_BREAK_RECEIVER_DIO;
import static frc.robot.Constants.Intake.INTAKE_ENCODER_CYCLES_PER_ROTATION;
import static frc.robot.Constants.Intake.INTAKE_INCHES_PER_REVOLUTION;
import static frc.robot.Constants.Intake.INTAKE_MAX_RPM;
import static frc.robot.Constants.Intake.INTAKE_MIN_RPM;
import static frc.robot.Constants.Intake.INTAKE_MOTOR_CAN_ID;
import static frc.robot.Constants.Intake.INTAKE_PCM_CHANNEL;
import static frc.robot.Constants.Intake.INTAKE_SPEED_TO_DRIVE_SPEED_RATIO;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.devices.RobotCargoCount;
import frc.robot.subsystems.intake.IntakeStateMachine.State;

public class Intake extends SubsystemBase {
    private final DoubleSupplier robotSpeedSupplier;
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    private final CANSparkMax intakeMotor;
    private final SparkMaxPIDController pidController;
    private final IntakeStateMachine stateMachine;
    private State autoExitState = null;
    private final AtomicReference<IntakeRequest> request = new AtomicReference<>(IntakeRequest.NO_REQUEST_PENDING);

    /**
     * This enum describes the steps in handling the extension and retraction of the
     * intake.
     */
    private enum IntakeRequest {
        /** Stowed or not, see {@link Intake#isRetracted()} */
        NO_REQUEST_PENDING,
        /** Extension (start gathering) of the retracted intake has been requested. */
        EXTENSION_REQUESTED,
        /** Retraction (stop gathering) of the extended intake has been requested. */
        RETRACTION_REQUESTED;
    }

    /**
     * @param robotSpeedSupplier a supplier of the current robot speed (inches per
     *                           second) that can be used to optimize the intake
     *                           speed.
     */
    public Intake(final DoubleSupplier robotSpeedSupplier) {
        this.robotSpeedSupplier = robotSpeedSupplier;
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, INTAKE_PCM_CHANNEL);
        this.beamBreak = new DigitalInput(INTAKE_BEAM_BREAK_RECEIVER_DIO);
        this.intakeMotor = new CANSparkMax(INTAKE_MOTOR_CAN_ID, MotorType.kBrushed);
        this.intakeMotor.restoreFactoryDefaults();
        this.intakeMotor.setIdleMode(IdleMode.kBrake);
        this.intakeMotor.getEncoder(
                SparkMaxRelativeEncoder.Type.kQuadrature,
                INTAKE_ENCODER_CYCLES_PER_ROTATION);
        this.pidController = this.intakeMotor.getPIDController();
        // TODO configure kP and kF for velocity control.
        this.pidController.setFF(0.1);
        this.pidController.setP(0.1);

        this.stateMachine = new IntakeStateMachine(this);
        setDefaultCommand(this.stateMachine.getDefaultCommand());
        new Trigger(this::isStateMachineRunning)
                .whenInactive(new InstantCommand(() -> request.set(IntakeRequest.NO_REQUEST_PENDING)));
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        SmartDashboard.putBoolean("Intake: ", !this.beamBreak.get());
        Command current = getCurrentCommand();
        SmartDashboard.putString("Intake State: ", current != null ? current.getName() : "<null>");
        SmartDashboard.putBoolean("Intake direction", intakeDirection());
    }

    public void extendIntake() {
        intakeSolenoid.set(true);
    }

    public void retractIntake() {
        intakeSolenoid.set(false);
    }

    public boolean isRetracted() {
        return !this.intakeSolenoid.get();
    }

    public double getIntakeSpeed() {
        return this.intakeMotor.get();
    }

    /**
     * Set the intake speed to a value proportional to the speed at which the robot
     * is moving with enforcement of minimum and maximum speeds.
     */
    public void setIntakeSpeed() {
        final double targetIntakeSpeed = this.robotSpeedSupplier.getAsDouble()
                * INTAKE_SPEED_TO_DRIVE_SPEED_RATIO;
        final double targetRevPerSec = targetIntakeSpeed / INTAKE_INCHES_PER_REVOLUTION;
        final double targetRPM = MathUtil.clamp(
                targetRevPerSec * 60.0,
                INTAKE_MIN_RPM,
                INTAKE_MAX_RPM);
        this.pidController.setReference(-targetRPM, ControlType.kVelocity);
    }

    public void stopIntakeMotor() {
        this.pidController.setReference(0.0, ControlType.kDutyCycle);
    }

    /**
     * @return true if there is a cargo at the ready to shoot sensor.
     */
    public boolean isCargoAtSensor() {
        return !beamBreak.get();
    }

    public boolean isSendingCargo() {
        final State currentState = this.stateMachine.getCurrentState();
        return (currentState == State.GATHERING_SEND)
                || (currentState == State.STOWED_SEND);
    }

    /**
     * To be used by manual commands when sensors are compromised.
     * 
     * @param speed the percent output (-1.0 to 1.0) to apply.
     */
    public void setManualSpeed(final double speed) {
        this.intakeMotor.set(speed);
    }

    // returns true if direciton is positive
    public boolean intakeDirection() {
        return this.intakeMotor.getOutputCurrent() >= 0;
    }

    /**
     * Called to request that the intake be extended for gathering.
     * 
     * @return true if the request was granted (no cargo at sensor and the robot is
     *         not full).
     */
    public boolean requestExtension() {
        return !this.isCargoAtSensor()
                && !RobotCargoCount.getInstance().isFull()
                && this.request.compareAndSet(IntakeRequest.NO_REQUEST_PENDING, IntakeRequest.EXTENSION_REQUESTED);
    }

    /**
     * A new instance is needed each time since these end up in command groups and a
     * command can only be in one command group.
     * 
     * @return a new command to request intake gathering.
     */
    public Command getAutoRequestExtensionCommand() {
        return new InstantCommand(this::requestExtension);
    }

    /**
     * Called to request that the intake be retracted to stop gathering.
     * 
     * @return true if the request was granted.
     */
    public boolean requestRetraction() {
        return !this.isRetracted()
                && this.request.compareAndSet(IntakeRequest.NO_REQUEST_PENDING, IntakeRequest.RETRACTION_REQUESTED);
    }

    boolean isExtensionRequested() {
        return this.request.get() == IntakeRequest.EXTENSION_REQUESTED;
    }

    boolean isRetractionRequested() {
        return this.request.get() == IntakeRequest.RETRACTION_REQUESTED;
    }

    boolean extensionHandled() {
        return this.request.compareAndSet(IntakeRequest.EXTENSION_REQUESTED, IntakeRequest.NO_REQUEST_PENDING);
    }

    boolean retractionHandled() {
        return this.request.compareAndSet(IntakeRequest.RETRACTION_REQUESTED, IntakeRequest.NO_REQUEST_PENDING);
    }

    /**
     * Sets the proper state for autonomous. Should only be called from autonomous
     * initialization.
     */
    public void enterAuto() {
        this.stateMachine.setNextInitialState(State.STOWED_EMPTY);
    }

    /**
     * Saves the last state in autonomous for start of teleop. Should only be called
     * from autonomous exit.
     */
    public void exitAuto() {
        this.autoExitState = this.stateMachine.getCurrentState();
    }

    /**
     * Sets the proper state for teleop. Should only be called from teleop
     * initialization.
     */
    public void enterTeleop() {
        if (this.autoExitState == null) {
            this.stateMachine.setNextInitialState(State.STOWED_EMPTY);
        } else {
            this.stateMachine.setNextInitialState(this.autoExitState);
            this.autoExitState = null;
        }
    }

    /**
     * @return true if the state machine is running and false otherwise.
     */
    public boolean isStateMachineRunning() {
        return this.stateMachine.getDefaultCommand().isScheduled();
    }
}
