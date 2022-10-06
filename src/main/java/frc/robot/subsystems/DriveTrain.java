package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;

/**
 * The drive train consists of three Neos on each side. Encoders are used for
 * path following in autonomous, as is a NavX gyro. Two line sensors are used to
 * stop at selected positions on the field.
 * 
 * Some notes on automating actions of the drive train. First, automation that
 * does not use WPILIB trajectory following, use of the {@link #resetDistance()}
 * and {@link #resetYaw()} methods before starting a position or rotational
 * movement respectively is generally a good idea. Given the odd angles at which
 * the robot will start most matches, robot relative movements and rotations
 * (not field relative) will be the easiest to manage. This can be useful for
 * autonomous and for aiming a shot based off vision feedback.
 * 
 * Second, for automation that does use WPILIB trajectory following (more
 * advanced autonomous mode routines), resetting at the start of a sequence
 * (there could be different API, this is TBD) is probably desired. Do NOT reset
 * inbetween a sequence of trajectory commands that are chained together in a
 * command group.
 */
public class DriveTrain extends SubsystemBase {

  private final CANSparkMax left1;
  private final CANSparkMax left2;
  private final CANSparkMax left3;
  private final CANSparkMax right1;
  private final CANSparkMax right2;
  private final CANSparkMax right3;

  private final MotorControllerGroup leftGroup;
  private final MotorControllerGroup rightGroup;
  private final DifferentialDrive diffDrive;

  private final RelativeEncoder leftNeoEncoder;
  private final Encoder leftEncoder;
  private final Encoder rightEncoder;

  private final Solenoid sidewinderSolenoid;
  private final WPI_TalonFX sidewinderMotor;

  private final AHRS navx;

  private final BooleanSupplier sidewinderOverride;
  private final PIDController strafeHorizonatal = new PIDController(Constants.SIDEWINDER_kP, 0, 0);

  private double lastRotation = 0.0;

  private final JoystickButton speedLimit;

  public DriveTrain (BooleanSupplier sidewinderOverride, JoystickButton speedLimit) {

    this.left1 = new CANSparkMax(Constants.LEFT_DRIVE_1_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.left1);

    this.left2 = new CANSparkMax(Constants.LEFT_DRIVE_2_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.left2);

    this.left3 = new CANSparkMax(Constants.LEFT_DRIVE_3_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.left3);

    this.right1 = new CANSparkMax(Constants.RIGHT_DRIVE_1_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.right1);

    this.right2 = new CANSparkMax(Constants.RIGHT_DRIVE_2_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.right2);

    this.right3 = new CANSparkMax(Constants.RIGHT_DRIVE_3_CAN_ID, MotorType.kBrushless);
    this.motorConfig(this.right3);

    this.leftGroup = new MotorControllerGroup(this.left1, this.left2, this.left3);
    this.rightGroup = new MotorControllerGroup(this.right1, this.right2, this.right3);
    this.rightGroup.setInverted(true);
    this.diffDrive = new DifferentialDrive(this.leftGroup, this.rightGroup);

    this.leftNeoEncoder = this.left1.getEncoder();

    this.leftEncoder = new Encoder(Constants.LEFT_DRIVE_A_CHANNEL, Constants.LEFT_DRIVE_B_CHANNEL, false, EncodingType.k4X);
    this.leftEncoder.setDistancePerPulse(Constants.DRIVE_INCHES_PER_PULSE);
    this.leftEncoder.setSamplesToAverage(5);

    // TODO Verify boolean value.
    this.rightEncoder = new Encoder(Constants.RIGHT_DRIVE_A_CHANNEL, Constants.RIGHT_DRIVE_B_CHANNEL, true, EncodingType.k4X);
    this.rightEncoder.setDistancePerPulse(Constants.DRIVE_INCHES_PER_PULSE);
    this.rightEncoder.setSamplesToAverage(5);

    this.sidewinderSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.SIDEWINDER_PCM_CHANNEL);
    this.sidewinderMotor = new WPI_TalonFX(Constants.SIDEWINDER_MOTOR_CAN_ID);
    this.sidewinderMotor.configFactoryDefault();
    //this.sidewinderMotor.configOpenloopRamp(0.5);
    this.sidewinderOverride = sidewinderOverride;

    this.navx = new AHRS(SPI.Port.kMXP);
    this.speedLimit = speedLimit;

    /*
    SmartDashboard.putNumber("1: ", this.left1.getAppliedOutput());
    SmartDashboard.putNumber("2: ", this.left2.getMotorTemperature());
    SmartDashboard.putNumber("3: ", this.left3.getOutputCurrent());
    SmartDashboard.putNumber("4: ", this.right1.getOutputCurrent());
    SmartDashboard.putNumber("5: ", this.right2.getOutputCurrent());
    SmartDashboard.putNumber("6: ", this.right3.getOutputCurrent());
    */

    //SmartDashboard.putNumber("Left Encoder: ", this.right);
    //SmartDashboard.putNumber("Right Encoder: ", this.right);
  }

  @Override
  public void periodic () {

    //SmartDashboard.putNumber("Distance from Encoders: ", this.getDistance());
    //SmartDashboard.putNumber("Left Encoder: ", this.getDistance());
    SmartDashboard.putNumber("NavX Angle", this.getHeading());
    //SmartDashboard.putNumber("Right Encoder: ", this.rightEncoder.getDistance());
  }

  /**
   * @param speed    the raw speed input.
   * @param rotation the raw rotaion input.
   * @param sidewind the sidewinder input. Only used if sidewinder is engaged.
   */
  public void arcadeDrive (double speed, final double rotation, final double sidewind) {

    double arcadeSpeed = speed;
    double arcadeRotation = 0.7 * rotation;

    if (this.speedLimit.get()) { 
      arcadeSpeed /= 2; 
      //arcadeRotation *= 0.7;
    }

    // Sidewind above threshold, disengage below, leave as is in gap.
    if ((Math.abs(sidewind) > Constants.SIDEWINDER_ENABLE_THRESHOLD) || this.sidewinderOverride.getAsBoolean()) {
      
      if (!this.sidewinderSolenoid.get()) {

        // Sidewider has deployed
        if (Math.abs(this.getHeading()) > 180) { this.resetYaw(); }
        this.resetHeading();
      }
      
      this.sidewinderSolenoid.set(true);
      Lights.getInstance().setColor(LEDMode.TWINKLES);
    } else if (Math.abs(sidewind) < Constants.SIDEWINDER_DISABLE_THRESHOLD) {

      this.sidewinderSolenoid.set(false);
    }

    if (this.sidewinderSolenoid.get()) {

      this.sidewinderMotor.set(ControlMode.PercentOutput, -(sidewind - (Math.signum(sidewind) * Constants.SIDEWINDER_OUTPUT_OFFSET)));
      
      if (rotation == 0.0) {

        if (this.lastRotation != 0.0) { 

          // Sidewinder has stopped swerving, but is still sidewinding.
          if (Math.abs(this.getHeading()) > 180) { this.resetYaw(); }
          this.resetHeading();
        }

        arcadeRotation = strafeHorizonatal.calculate(this.getHeading() % 360, 0.0);
      }
    }

    this.lastRotation = rotation;
    this.diffDrive.arcadeDrive(arcadeSpeed, arcadeRotation, true);
  }

  public void stop () {

    this.arcadeDrive(0.0, 0.0, 0.0);
  }

  /**
   * Resets the distance sensors (aka encoders) of the drive train.
   */
  public void resetDistance () {

    this.leftNeoEncoder.setPosition(0.0);
    this.leftEncoder.reset();
    this.rightEncoder.reset();
  }

  /**
   * Note that the returned inches are the average of the left and right side
   * sensors.
   * 
   * @return the distance traveled in inches since the last
   *         {@link #resetDistance()} call.
   */
  public double getDistance () {

    //return (this.leftEncoder.getDistance() + this.rightEncoder.getDistance()) / 2.0;
    //return this.leftEncoder.getDistance();
    return this.leftNeoEncoder.getPosition() / 8.68 * 19.24;
  }

  /**
   * Note that the returned inches per second are the average of the left and
   * right side sensors.
   * 
   * @return the rate of travel in inches per second.
   */
  public double getRate () {

    //return (this.leftEncoder.getRate() + this.rightEncoder.getRate()) / 2.0;
    //return this.leftEncoder.getRate();
    return this.leftNeoEncoder.getVelocity() / 8.68 * 19.24 / 60.0;
  }

  /**
   * Configures the getHeading method to return 0. For time limited robot oriented
   * movements, prefer this method over resetYaw.
   */
  public void resetHeading () {

    this.navx.setAngleAdjustment(-getYaw());
    // this.navx.setAngleAdjustment(-getHeading() + this.navx.getAngleAdjustment());
    // this.navx.setAngleAdjustment(-(Math.floor(this.getHeading() / 360) * 360) - this.getYaw());
  }

  /**
   * Returns the current adjusted robot oriented heading. For time limited robot
   * oriented movements, prefer this method over getYaw.
   * 
   * @return the current robot oriented heading.
   */
  public double getHeading () {

    return this.navx.getAngle();
  }

  /**
   * Resets the robot's yaw to 0.
   * 
   * WARNING: Use this only in extreme cases. Sometimes the reset can take time
   * and getYaw could return non-zero values for a short time after this is
   * called. For time limited robot oriented movements, prefer resetHeading and
   * getHeading.
   */
  public void resetYaw() {

    this.navx.reset();
  }

  /**
   * This should rarely be used. For time limited robot oriented movements, prefer
   * resetHeading and getHeading.
   * 
   * @return the current yaw value in degrees (-180 to 180).
   */
  public float getYaw () {

    return this.navx.getYaw();
  }

  /**
   * Configures a motor during construction.
   * 
   * @param motor the motor to configure.
   */
  private void motorConfig (final CANSparkMax motor) {

    motor.restoreFactoryDefaults();
    motor.setIdleMode(IdleMode.kBrake);
    motor.setOpenLoopRampRate(0.25);
    // TODO Determine and set current limit.
  }
}
