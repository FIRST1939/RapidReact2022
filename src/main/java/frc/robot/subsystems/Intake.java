package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

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
    private boolean manualMode = false;

    /**
     * @param robotSpeedSupplier a supplier of the current robot speed (inches per
     *                           second) that can be used to optimize the intake
     *                           speed.
     */
    public Intake(final DoubleSupplier robotSpeedSupplier) {
        this.robotSpeedSupplier = robotSpeedSupplier;
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.INTAKE_PCM_CHANNEL);
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
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }

    public void extendIntake() {
        intakeSolenoid.set(true);
    }

    public void retractIntake() {
        intakeSolenoid.set(false);
    }

    public boolean isRetracted() {
        return this.intakeSolenoid.get();
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
        this.pidController.setReference(targetRPM, ControlType.kVelocity);
    }

    public void stopIntakeMotor() {
        this.pidController.setReference(0.0, ControlType.kVelocity);
    }

    /**
     * @return true if there is a cargo at the ready to shoot sensor.
     */
    public boolean isCargoAtSensor() {
        return !beamBreak.get(); // TODO verify this negation.
    }

    /**
     * To be used by manual commands when sensors are compromised.
     * 
     * @param speed the percent output (-1.0 to 1.0) to apply.
     */
    public void setManualSpeed(final double speed) {
        this.intakeMotor.set(speed);
    }

    /**
     * @return true if manual mode commands (vs state machine) are running.
     */
    public boolean isManualMode() {
        return this.manualMode;
    }

    /**
     * @param manualMode true to indicate that manual mode commands (vs state
     *                   machine) are running.
     */
    public void setManualMode(final boolean manualMode) {
        this.manualMode = manualMode;
    }
}
