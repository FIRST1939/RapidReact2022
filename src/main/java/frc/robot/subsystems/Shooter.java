package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;

public class Shooter extends SubsystemBase {

    private static Shooter shooterInstance = null;

    private final Solenoid shooterSolenoid;
    private final WPI_TalonFX shooterFlywheel;

    private Constants.SHOTS shot = Constants.SHOTS.fenderHigh;
    private int lastSetVelocity = 0;
    private int velocityInRangeCount = 0;

    private Shooter () {

        this.shooterSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.SHOOTER_PCM_CHANNEL);
        this.shooterFlywheel = new WPI_TalonFX(Constants.SHOOTER_FLYWHEEL_CAN_ID);

        this.shooterFlywheel.configFactoryDefault();
        this.shooterFlywheel.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
        this.shooterFlywheel.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 20);
        this.shooterFlywheel.configAllowableClosedloopError(0, Constants.SHOOTER_VELOCITY_ERROR / 2);
        this.shooterFlywheel.configNominalOutputForward(0);
        this.shooterFlywheel.configNominalOutputReverse(0);
        this.shooterFlywheel.configPeakOutputForward(1);
        this.shooterFlywheel.configPeakOutputReverse(-1);

        this.shooterFlywheel.config_kF(0, .10792);
        this.shooterFlywheel.config_kP(0, .0164);

        this.shooterFlywheel.setInverted(true);
    }

    /**
     * @return the current instance of the shooter subsystem
     */
    public static final synchronized Shooter getInstance () {

        if (shooterInstance == null) { shooterInstance = new Shooter(); }
        return shooterInstance;
    }

    public void cargoShot (final int velocity, final boolean hood) {

        this.setHood(hood);
        this.setVelocity(velocity);
    }

    public void cargoShot () {

        this.cargoShot(this.getShot().velocity, this.getShot().hood);
    }

    /**
     * @param shot the shot type that we are preparing.
     */
    public void cargoShot (final Constants.SHOTS shot) {

        this.shot = shot;
        this.cargoShot();
    }

    public Constants.SHOTS getShot () {

        return this.shot;
    }

    public void idle () { 

        this.cargoShot(Constants.SHOTS.idle);
    }

    private void setHood (final boolean hood) { 

        this.shooterSolenoid.set(hood); 
    }
    
    private void setVelocity (int velocity) {

        if (velocity >= 0) {

            if (lastSetVelocity != velocity) {

                this.shooterFlywheel.set(ControlMode.Velocity, velocity);
                this.lastSetVelocity = velocity;
                this.velocityInRangeCount = 0;
            }

            Lights.getInstance().setColor(LEDMode.CONFETTI);
        }
    }

    @Override
    public void periodic () {

        double currentVelocity = shooterFlywheel.getSelectedSensorVelocity()/2;
        //double currentClosedLoopError = shooterFlywheel.getClosedLoopError();
        //SmartDashboard.putNumber("Shooter Velocity", currentVelocity);
        //SmartDashboard.putNumber("Shooter Error", currentClosedLoopError);

        if (Math.abs(Math.abs(currentVelocity) - this.lastSetVelocity) <= this.lastSetVelocity * 0.04) {

            this.velocityInRangeCount++;
        } else {

            this.velocityInRangeCount = 0;
        }
    }
    
    public boolean isShooterReady () {

        return this.velocityInRangeCount >= 5;
    }

    /**
     * @return true if hood is up false otherwise
     */
    public boolean isHoodUp () { 

        return this.shooterSolenoid.get();
    }
}
