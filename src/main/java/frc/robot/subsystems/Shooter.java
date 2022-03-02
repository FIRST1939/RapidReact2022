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

    // Creates a new shooter.
    private Shooter () {

        // Create and configure shooter elements.
        shooterSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.SHOOTER_PCM_CHANNEL);
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

    /**
     * @param shot the shot type that we are preparing.
     */
    public void cargoShot (final Constants.SHOTS shot) {

        setHood(shot.hood);
        setVelocity(shot.velocity);
    }

    // TODO find right time to call
    public void stop () { 

        cargoShot(Constants.SHOTS.idle);
    }

    public void setHood (final boolean hood) { 

        shooterSolenoid.set(hood); 
    }

    
    public boolean isShooterReady () {

        return Math.abs(shooterFlywheel.getClosedLoopError()) < Constants.SHOOTER_VELOCITY_ERROR;
    }
    
    public void setVelocity (int velocity) {

        shooterFlywheel.set(ControlMode.Velocity, velocity);
    }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp (){ 

        return shooterSolenoid.get();
    }
}
