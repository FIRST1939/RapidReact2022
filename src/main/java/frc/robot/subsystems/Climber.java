package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Climber extends SubsystemBase {

    private static Climber climberInstance = null;

    // Climber elements.
    private final WPI_TalonFX climberMotor;
    private final DoubleSolenoid climberPiston;

    private boolean isPistonExtended = false;

    // Creates a new climber.
    private Climber () {

        // Create and configure climber elements.
        climberMotor = new WPI_TalonFX(Constants.CLIMBER_MOTOR_CAN_ID);
        climberPiston = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.CLIMBER_PISTON_FORWARD, Constants.CLIMBER_PISTON_REVERSE);

        climberMotor.configFactoryDefault();
        climberMotor.setNeutralMode(NeutralMode.Brake);
        climberMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 30);
        climberMotor.configNominalOutputForward(0,30);
        climberMotor.configNominalOutputReverse(0,30);
        climberMotor.configPeakOutputForward(1,30);
        climberMotor.configPeakOutputReverse(-1,30);

        climberMotor.config_kF(0, .10792, 30);
        climberMotor.config_kP(0, .0164, 30);

        setHome();
    }

    /**
     * @return the current instance of the climber subsystem
     */
    public static final synchronized Climber getInstance () {

        if (climberInstance == null) { climberInstance = new Climber(); }
        return climberInstance;
    }

    @Override
    public void periodic () {
        SmartDashboard.putNumber("Climber Encoder Clicks: ", this.getMotorPosition());
    }

    public void setMotor (int velocity) {

        climberMotor.set(ControlMode.Velocity, velocity);
    }

    public void setPiston (Boolean piston) {

        DoubleSolenoid.Value pistonValue;
        if (piston != null) {
            if (piston.equals(true)) { 
                pistonValue = DoubleSolenoid.Value.kForward; 
                isPistonExtended = true;
            }
            else { 
                pistonValue = DoubleSolenoid.Value.kReverse; 
                isPistonExtended = false;
            }
        } else { pistonValue = DoubleSolenoid.Value.kOff; }

        climberPiston.set(pistonValue);
    }

    public void setHome () {
        
        climberMotor.setSelectedSensorPosition(0);
    }

    public double getMotorPosition () {

        return climberMotor.getSelectedSensorPosition();
    }

    public boolean isPistonExtended () {

        return this.isPistonExtended;
    }
}
