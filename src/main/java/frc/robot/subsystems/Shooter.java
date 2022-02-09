package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase{

    // Shooter elements.
    private final Solenoid shooterSolenoid;
    private final WPI_TalonFX shooterFlywheel;

    // Creates a new shooter.
    public Shooter () {

        // Create and configure shooter elements.
        shooterSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.SHOOTER_PCM_CHANNEL);
        shooterFlywheel = new WPI_TalonFX(Constants.SHOOTER_FLYWHEEL_CAN_ID);
        shooterFlywheel.configFactoryDefault();
    }

    /**
     * @param distance the current shooting ring
     */
    public void cargoShot (final int distance) {

        // TODO shooting ring ratios
        double velocity = 0.0;
        boolean hood = isHoodUp();

        if (distance == 1) {

            velocity = 0.5;
            hood = false;
        } else if (distance == 2) {

            velocity = 0.8;
            hood = false;
        } else {

            velocity = 1.0;
            hood = true;
        }

        setHood(hood);
        shooterFlywheel.set(ControlMode.Velocity, velocity);
    }

    public void stop () { cargoShot(0); }

    public void setHood (final boolean hood) { shooterSolenoid.set(hood); }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp () { return shooterSolenoid.get(); }
}
