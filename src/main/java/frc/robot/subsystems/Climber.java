package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Climber extends SubsystemBase {

    private static Climber climberInstance = null;

    // Climber elements.
    private final WPI_TalonFX climberMotor;
    private final DoubleSolenoid climberPiston;

    // Creates a new climber.
    private Climber () {

        // Create and configure climber elements.
        climberMotor = new WPI_TalonFX(Constants.CLIMBER_MOTOR_CAN_ID);
        climberPiston = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.CLIMBER_PISTON_FORWARD, Constants.CLIMBER_PISTON_REVERSE);

        climberMotor.configFactoryDefault();
        climberMotor.setNeutralMode(NeutralMode.Brake);

        // The climber must be winched before starting the robot.
        setHome();
    }

    /**
     * @return the current instance of the climber subsystem
     */
    public static final synchronized Climber getInstance () {

        if (climberInstance == null) { climberInstance = new Climber(); }
        return climberInstance;
    }

    public void setMotor (int velocity) {

        climberMotor.set(ControlMode.Velocity, velocity);
    }

    public void setPiston (Boolean piston) {

        DoubleSolenoid.Value pistonValue;
        if (piston.equals(true)) { pistonValue = DoubleSolenoid.Value.kForward; }
        else if (piston.equals(false)) { pistonValue = DoubleSolenoid.Value.kReverse; }
        else { pistonValue = DoubleSolenoid.Value.kOff; }

        climberPiston.set(pistonValue);
    }

    private void setHome () {
        
        climberMotor.setSelectedSensorPosition(0);
    }

    private double getMotorPosition () {

        return climberMotor.getSelectedSensorPosition();
    }

    public boolean isMotorFullyRetracted () {

        return getMotorPosition() == 0;
    }

    public boolean isMotorFullyExtended () {

        return getMotorPosition() == Constants.CLIMBER_ENCODER_EXTEND_CLICKS;
    }

    public boolean isPistonExtended () {

        return climberPiston.get() == DoubleSolenoid.Value.kForward;
    }
}
