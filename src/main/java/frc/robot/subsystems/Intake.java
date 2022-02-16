package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

//TODO Import correct motor(s) and adjust as needed, waiting on mechanical


public class Intake extends SubsystemBase{
    private final Solenoid intakeSolenoid;
    private final DigitalInput beamBreak;
    
    //TODO Intake motor(s)

    public Intake() {
        this.intakeSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.INTAKE_PCM_CHANNEL);
        this.beamBreak = new DigitalInput(Constants.INTAKE_BEAM_BREAK_RECEIVER_DIO);
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

    public void setRollerSpeed() {
        //TODO Turn on intake roller motor
        //TODO Scale rotation peed based on robot velocity
    }

    public void stopRoller() {
        //TODO Stop intake roller motor
    }

    //Deploy includes both roller and frame
    public void deployIntake() {
        extendIntake();
        setRollerSpeed();
    }

    public void undeployIntake() {
        retractIntake();
        stopRoller();
    }

    public void setToSendVelocity() {
        //TODO Set intake belt motor speed to constant send velocity
    }

    public void stopIntakeBelt() {
        //TODO Stop
    }
   

    /**
     * @return true if there is a cargo at the ready to shoot sensor.
     */
    public boolean isCargoAtSensor() {
        return !beamBreak.get(); // TODO verify this negation.
    }

    

}
