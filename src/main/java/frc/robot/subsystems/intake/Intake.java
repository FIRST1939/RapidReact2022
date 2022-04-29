package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import frc.robot.subsystems.RobotCargoCount;
import frc.robot.subsystems.intake.IntakeStateMachine.State;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Intake extends SubsystemBase {
    private final DoubleSupplier robotSpeedSupplier;
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    private final CANSparkMax intakeMotor;
    private final SparkMaxPIDController pidController;
    private final IntakeStateMachine stateMachine;
    private State autoExitState = null;

    /**
     * @param robotSpeedSupplier a supplier of the current robot speed (inches per
     *                           second) that can be used to optimize the intake
     *                           speed.
     */
    public Intake(final DoubleSupplier robotSpeedSupplier) {
        this.robotSpeedSupplier = robotSpeedSupplier;
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.INTAKE_PCM_CHANNEL);
        this.beamBreak = new DigitalInput(Constants.INTAKE_BEAM_BREAK_RECEIVER_DIO);
        this.intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_CAN_ID, MotorType.kBrushed);
        this.intakeMotor.restoreFactoryDefaults();
        this.intakeMotor.setIdleMode(IdleMode.kBrake);
        this.intakeMotor.getEncoder(
                SparkMaxRelativeEncoder.Type.kQuadrature,
                Constants.INTAKE_ENCODER_CYCLES_PER_ROTATION);
        this.pidController = this.intakeMotor.getPIDController();
        // TODO configure kP and kF for velocity control.
        this.pidController.setFF(0.1);
        this.pidController.setP(0.1);

        this.stateMachine = new IntakeStateMachine(this);
        setDefaultCommand(this.stateMachine.getDefaultCommand());
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
                * Constants.INTAKE_SPEED_TO_DRIVE_SPEED_RATIO;
        final double targetRevPerSec = targetIntakeSpeed / Constants.INTAKE_INCHES_PER_REVOLUTION;
        final double targetRPM = MathUtil.clamp(
                targetRevPerSec * 60.0,
                Constants.INTAKE_MIN_RPM,
                Constants.INTAKE_MAX_RPM);
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
     * @return true if the request was granted.
     */
    public boolean requestExtension() {
        if (!this.isCargoAtSensor() && !RobotCargoCount.getInstance().isFull()) {
            this.stateMachine.forceToState(State.GATHERING_EMPTY);
            return true;
        }
        return false;
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
        boolean requestGranted = false;
        if (!this.isRetracted()) {
            // In a gathering or at sensor state. Not stowed.
            final State currentState = this.stateMachine.getCurrentState();
            switch (currentState) {
                case GATHERING_EMPTY:
                    this.stateMachine.forceToState(State.STOWED_EMPTY);
                    requestGranted = true;
                    break;

                case AT_SENSOR:
                    if (RobotCargoCount.getInstance().isFull()) {
                        this.stateMachine.forceToState(State.STOWED_HOLD);
                    } else {
                        this.stateMachine.forceToState(State.STOWED_SEND);
                    }
                    requestGranted = true;
                    break;

                case GATHERING_SEND:
                    this.stateMachine.forceToState(State.STOWED_SEND);
                    requestGranted = true;
                    break;

                default:
                    break;
            }
        }
        return requestGranted;
    }

    public void enterAuto() {
        this.stateMachine.forceToState(State.STOWED_EMPTY);
    }

    public void exitAuto() {
        this.autoExitState = this.stateMachine.getCurrentState();
    }

    public void enterTeleop() {
        if (this.autoExitState == null) {
            this.stateMachine.forceToState(State.STOWED_EMPTY);
            RobotCargoCount.getInstance().setCount(0);
        } else {
            this.stateMachine.forceToState(this.autoExitState);
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
