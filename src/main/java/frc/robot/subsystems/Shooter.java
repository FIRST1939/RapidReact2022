package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;

public class Shooter extends SubsystemBase {

    private static Shooter shooterInstance = null;

    // Shooter elements.
    private final Solenoid shooterSolenoid;
    private final WPI_TalonFX shooterFlywheel;

    private Constants.SHOTS shot = Constants.SHOTS.fenderHigh;
    private int lastSetVelocity = 0;
    private int velocityInRangeCount = 0;

    // Creates a new shooter.
    private Shooter () {

        // Create and configure shooter elements.
        shooterSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.SHOOTER_PCM_CHANNEL);
        shooterFlywheel = new WPI_TalonFX(Constants.SHOOTER_FLYWHEEL_CAN_ID);

        shooterFlywheel.configFactoryDefault();
        shooterFlywheel.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
        shooterFlywheel.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 20);
        shooterFlywheel.configAllowableClosedloopError(0, Constants.SHOOTER_VELOCITY_ERROR / 2);
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
    
    private void setVelocity(int velocity) {
        if (lastSetVelocity != velocity) {
            shooterFlywheel.set(ControlMode.Velocity, velocity);
            this.lastSetVelocity = velocity;
            this.velocityInRangeCount = 0;
        }
        Lights.getInstance().setColor(LEDMode.CONFETTI);
    }

    @Override
    public void periodic() {
        double currentVelocity = shooterFlywheel.getSelectedSensorVelocity()/2;
        //double currentClosedLoopError = shooterFlywheel.getClosedLoopError();
        //SmartDashboard.putNumber("Shooter Velocity", currentVelocity);
        //SmartDashboard.putNumber("Shooter Error", currentClosedLoopError);
        if (Math.abs(Math.abs(currentVelocity)
                - this.lastSetVelocity) <= this.lastSetVelocity*0.04) {
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
    public boolean isHoodUp (){ 

        return shooterSolenoid.get();
    }
}
