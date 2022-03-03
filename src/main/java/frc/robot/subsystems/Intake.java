package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Intake extends SubsystemBase {
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    private final WPI_TalonSRX intakeMotor;
    private boolean manualMode = false;

    public Intake() {
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.INTAKE_PCM_CHANNEL);
        this.beamBreak = new DigitalInput(Constants.INTAKE_BEAM_BREAK_RECEIVER_DIO);
        this.intakeMotor = new WPI_TalonSRX(Constants.INTAKE_MOTOR_CAN_ID);
        this.intakeMotor.configFactoryDefault();
        // TODO configure kP and kF for velocity control.
        // Include configuration of attached encoder.
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }

    public void extendIntake() {
        intakeSolenoid.set(false);
    }

    public void retractIntake() {
        intakeSolenoid.set(true);
    }

    public boolean isRetracted() {
        return this.intakeSolenoid.get();
    }

    public void setIntakeSpeed(double value) {
        intakeMotor.set(ControlMode.PercentOutput, value);
        // TODO Scale rotation speed based on robot velocity
    }

    public void stopIntakeMotor() {
        setIntakeSpeed(0);
    }

    /**
     * @return Deploy and stow includes both roller and frame
     */
    public void deployIntake(double intakeSpeed) {
        extendIntake();
        setIntakeSpeed(intakeSpeed);
        // TODO Possibly remove parameter? Base roller/belt speed on drivetrain speed?
    }

    public void stowIntake() {
        retractIntake();
        stopIntakeMotor();
    }

    public void setToSendVelocity(double beltVelocity) {
        intakeMotor.set(ControlMode.PercentOutput, beltVelocity);
        // TODO Possibly remove parameter
    }

    public boolean getIntakeDeployment() {
        return intakeSolenoid.get(); // TODO Check possible inversion needed
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
