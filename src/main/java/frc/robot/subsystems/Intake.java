package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Intake extends SubsystemBase {
    private final DoubleSupplier robotSpeedSupplier;
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    private final WPI_TalonSRX intakeMotor;
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
        this.intakeMotor = new WPI_TalonSRX(Constants.INTAKE_MOTOR_CAN_ID);
        this.intakeMotor.configFactoryDefault();
        this.intakeMotor.setNeutralMode(NeutralMode.Brake);
        // TODO configure kP and kF for velocity control.
        // Include configuration of attached encoder.
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
        double targetIntakeSpeed = this.robotSpeedSupplier.getAsDouble() * Constants.INTAKE_SPEED_TO_DRIVE_SPEED_RATIO;
        double targetPulsePerSecond = targetIntakeSpeed / Constants.INTAKE_INCHES_PER_PULSE;
        double targetPulsePer100ms = targetPulsePerSecond / 10.0;
        int targetIntakeClicksPer100ms = (int) targetPulsePer100ms;
        targetIntakeClicksPer100ms = MathUtil.clamp(
            targetIntakeClicksPer100ms,
            Constants.INTAKE_MIN_CLICKS_PER_100MS,
            Constants.INTAKE_MAX_CLICKS_PER_100MS);
        intakeMotor.set(ControlMode.Velocity, targetIntakeClicksPer100ms);
    }

    public void stopIntakeMotor() {
        intakeMotor.set(ControlMode.Velocity, 0.0);
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
        this.intakeMotor.set(ControlMode.PercentOutput, speed);
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
