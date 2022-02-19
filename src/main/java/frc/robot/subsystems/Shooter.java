package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
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

    }

    /**
     * @return the current instance of the shooter subsystem
     */
    public static final synchronized Shooter getInstance () {

        if (shooterInstance == null) { shooterInstance = new Shooter(); }
        return shooterInstance;
    }

    /**
     * @param distance the current shooting ring
     */
    public void cargoShot (final int distance) {

        int velocity = Constants.SHOOTER_DEFAULT_VELOCITY;
        boolean hood = Constants.SHOOTER_DEFAULT_HOOD;

        if (distance == 1) {

            velocity = Constants.SHOOTER_ONE_VELOCITY;
            hood = Constants.SHOOTER_ONE_HOOD;
        } else if (distance == 2) {

            velocity = Constants.SHOOTER_TWO_VELOCITY;
            hood = Constants.SHOOTER_TWO_HOOD;
        } else if (distance == 3) {

            velocity = Constants.SHOOTER_THREE_VELOCITY;
            hood = Constants.SHOOTER_THREE_HOOD;
        }

        setHood(hood);
        shooterFlywheel.set(ControlMode.Velocity, velocity);
    }

    public void stop () { 
        cargoShot(0); 
    }

    public void setHood (final boolean hood) { 
        shooterSolenoid.set(hood); 
    }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp (){ 
        return shooterSolenoid.get(); 
    }
}
