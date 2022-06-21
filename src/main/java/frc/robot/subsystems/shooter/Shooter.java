package frc.robot.subsystems.shooter;

import static frc.robot.Constants.Shooter.SHOOTER_FLYWHEEL_CAN_ID;
import static frc.robot.Constants.Shooter.SHOOTER_PCM_CHANNEL;
import static frc.robot.Constants.Shooter.SHOOTER_VELOCITY_ERROR;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LEDMode;
import frc.robot.devices.Lights;
import frc.robot.devices.Targeting;

public class Shooter extends SubsystemBase {
    private static Shooter shooterInstance = null;

    // Shooter elements.
    private final Solenoid shooterSolenoid;
    private final WPI_TalonFX shooterFlywheel;
    private final ShooterTargeting targeting = new ShooterTargeting();

    private Shots shot = Shots.fenderHigh;
    private int lastSetVelocity = 0;
    private int velocityInRangeCount = 0;

    // Creates a new shooter.
    private Shooter() {
        // Create and configure shooter elements.
        shooterSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, SHOOTER_PCM_CHANNEL);
        shooterFlywheel = new WPI_TalonFX(SHOOTER_FLYWHEEL_CAN_ID);

        shooterFlywheel.configFactoryDefault();
        shooterFlywheel.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
        shooterFlywheel.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 20);
        shooterFlywheel.configAllowableClosedloopError(0, SHOOTER_VELOCITY_ERROR);
        shooterFlywheel.configNominalOutputForward(0);
        shooterFlywheel.configNominalOutputReverse(0);
        shooterFlywheel.configPeakOutputForward(1);
        shooterFlywheel.configPeakOutputReverse(-1);

        shooterFlywheel.config_kF(0, .10792);
        shooterFlywheel.config_kP(0, .0164);

        shooterFlywheel.setInverted(true);
    }

    /**
     * @return the current instance of the shooter subsystem
     */
    public static final synchronized Shooter getInstance() {
        if (shooterInstance == null) {
            shooterInstance = new Shooter();
        }
        return shooterInstance;
    }

    public void cargoShot(final int velocity, final boolean hood) {

        setHood(hood);
        setVelocity(velocity);
    }

    /**
     * Reapplies the last set shooter velocity. Typically used to come out of idle.
     */
    public void cargoShot() {
        cargoShot(getShot().getVelocity(), getShot().isHoodExtended());
    }

    /**
     * @param shot the shot type that we are preparing.
     */
    public void cargoShot(final Shots shot) {
        this.shot = shot;
        cargoShot();
    }

    public Shots getShot() {
        return shot;
    }

    /**
     * Set the shooter velocity to a slower idle speed.
     */
    public void idle() {
        this.cargoShot(Shots.idle);
    }

    private void setHood(final boolean hood) {
        shooterSolenoid.set(hood);
    }

    private void setVelocity(int velocity) {
        if (velocity >= 0) {
            if (lastSetVelocity != velocity) {
                shooterFlywheel.set(ControlMode.Velocity, velocity);
                this.lastSetVelocity = velocity;
                this.velocityInRangeCount = 0;
            }
            Lights.getInstance().setColor(LEDMode.CONFETTI);
        }
    }

    @Override
    public void periodic() {
        double currentVelocity = shooterFlywheel.getSelectedSensorVelocity() / 2;
        if (Math.abs(Math.abs(currentVelocity)
                - this.lastSetVelocity) <= this.lastSetVelocity * 0.04) {
            this.velocityInRangeCount++;
        } else {
            this.velocityInRangeCount = 0;
        }
    }

    public boolean isShooterReady() {
        return this.velocityInRangeCount >= 5;
    }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp() {
        return shooterSolenoid.get();
    }

    /**
     * @return the {@link Targeting} implementation for this shooter.
     */
    public Targeting getTargeting() {
        return this.targeting;
    }
}
