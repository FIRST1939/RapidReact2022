package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {

    private static Shooter shooterInstance = null;

    // Shooter elements.
    private final Solenoid shooterSolenoid;
    private final WPI_TalonFX shooterFlywheel;

    private Constants.SHOTS shot;

    // Creates a new shooter.
    private Shooter () {

        // Create and configure shooter elements.
        shooterSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.SHOOTER_PCM_CHANNEL);
        shooterFlywheel = new WPI_TalonFX(Constants.SHOOTER_FLYWHEEL_CAN_ID);

        shooterFlywheel.configFactoryDefault();
        shooterFlywheel.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 30);
        shooterFlywheel.configNominalOutputForward(0,30);
        shooterFlywheel.configNominalOutputReverse(0,30);
        shooterFlywheel.configPeakOutputForward(1,30);
        shooterFlywheel.configPeakOutputReverse(-1,30);

        shooterFlywheel.config_kF(0, .10792, 30);
        shooterFlywheel.config_kP(0, .0164, 30);

        shooterFlywheel.setInverted(true);
    }

    /**
     * @return the current instance of the shooter subsystem
     */
    public static final synchronized Shooter getInstance () {

        if (shooterInstance == null) { shooterInstance = new Shooter(); }
        return shooterInstance;
    }

    public void cargoShot () {

        setHood(getShot().hood);
        setVelocity(getShot().velocity);
    }

    /**
     * @param shot the shot type that we are preparing.
     */
    public void cargoShot (final Constants.SHOTS shot) {

        this.shot = shot;
        cargoShot();
    }

    public Constants.SHOTS getShot () {

        return shot;
    }

    public void idle () { 

        setVelocity(Constants.SHOTS.idle.velocity);
    }

    private void setHood (final boolean hood) { 

        shooterSolenoid.set(hood); 
    }
    
    private void setVelocity (int velocity) {

        shooterFlywheel.set(ControlMode.Velocity, velocity);
    }
    
    public boolean isShooterReady () {

        return Math.abs(shooterFlywheel.getClosedLoopError()) < Constants.SHOOTER_VELOCITY_ERROR;
    }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp (){ 

        return shooterSolenoid.get();
    }
}
