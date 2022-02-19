package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

import javax.net.ssl.X509ExtendedKeyManager;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

//TODO Import correct motor(s) and adjust as needed, waiting on mechanical


public class Intake extends SubsystemBase{
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    private final TalonSRX rollerMotor; 
    private final TalonSRX beltMotor;
    //TODO Intake motor(s)

    public Intake() {
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.INTAKE_PCM_CHANNEL);
        this.beamBreak = new DigitalInput(Constants.INTAKE_BEAM_BREAK_RECEIVER_DIO);
        this.rollerMotor = new TalonSRX(Constants.INTAKE_ROLLER_CAN_ID);
        this.beltMotor = new TalonSRX(Constants.INTAKE_BELT_CAN_ID);
        //TODO Intake motor(s)
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

    public void setRollerSpeed(double value) {
        rollerMotor.set(ControlMode.PercentOutput, value);
        //TODO Scale rotation speed based on robot velocity and rewrite function
    }

    public void stopRoller() {
        setRollerSpeed(0);
    }

    /**
     * @return Deploy and stow includes both roller and frame
     */
    public void deployIntake(double rollerSpeed) {
        extendIntake();
        setRollerSpeed(rollerSpeed);
    }

    public void stowIntake() {
        retractIntake();
        stopRoller();
    }

    public void setToSendVelocity(double beltVelocity) {
        beltMotor.set(ControlMode.PercentOutput, beltVelocity);
    }

    public void stopIntakeBelt() {
        setToSendVelocity(0);
    }

    public boolean getIntakeDeployment() {
        return intakeSolenoid.get(); //TODO Check possible inversion needed
    }


    /**
     * @return true if there is a cargo at the ready to shoot sensor.
     */
    public boolean isCargoAtSensor() {
        return !beamBreak.get(); // TODO verify this negation.
    }

    

}
