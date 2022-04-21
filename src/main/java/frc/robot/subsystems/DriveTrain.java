// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;

/**
 * The drive train consists of three Neos on each side. Encoders are used for
 * path following in autonomous, as is a NavX gyro. Two line sensors are used to
 * stop at selected positions on the field.
 * 
 * <p>
 * Some notes on automating actions of the drive train. First, automation that
 * does not use WPILIB trajectory following, use of the {@link #resetDistance()}
 * and {@link #resetYaw()} methods before starting a position or rotational
 * movement respectively is generally a good idea. Given the odd angles at which
 * the robot will start most matches, robot relative movements and rotations
 * (not field relative) will be the easiest to manage. This can be useful for
 * autonomous and for aiming a shot based off vision feedback.
 * </p>
 * 
 * <p>
 * Second, for automation that does use WPILIB trajectory following (more
 * advanced autonomous mode routines), resetting at the start of a sequence
 * (there could be different API, this is TBD) is probably desired. Do NOT reset
 * inbetween a sequence of trajectory commands that are chained together in a
 * command group.
 * </p>
 */
public class DriveTrain extends SubsystemBase {
  // Motors, 3 on a side.
  private final CANSparkMax left1;
  private final CANSparkMax left2;
  private final CANSparkMax left3;
  private final CANSparkMax right1;
  private final CANSparkMax right2;
  private final CANSparkMax right3;

  // Differential drive objects and boolean to activate 3rd pushing motor.
  private final MotorControllerGroup leftGroup;
  private final MotorControllerGroup rightGroup;
  private final DifferentialDrive diffDrive;

  // Drive train encoders.
  private final RelativeEncoder leftNeoEncoder;
  private final Encoder leftEncoder;
  private final Encoder rightEncoder;

  // Side winder related objects.
  private final Solenoid sidewinderSolenoid;
  private final WPI_TalonFX sidewinderMotor;

  private final AHRS navx;

  private final BooleanSupplier sidewinderOverride;
  private final PIDController strafeHorizonatal = new PIDController(Constants.SIDEWINDER_kP, 0, 0);

  private double lastRotation = 0.0;

  private final JoystickButton speedLimit;

  /**
   * Creates a new drive train.
   */
  public DriveTrain(BooleanSupplier sidewinderOverride, JoystickButton speedLimit) {
    // Create and configure individual motors.
    left1 = new CANSparkMax(Constants.LEFT_DRIVE_1_CAN_ID, MotorType.kBrushless);
    motorConfig(left1);
    left2 = new CANSparkMax(Constants.LEFT_DRIVE_2_CAN_ID, MotorType.kBrushless);
    motorConfig(left2);
    left3 = new CANSparkMax(Constants.LEFT_DRIVE_3_CAN_ID, MotorType.kBrushless);
    motorConfig(left3);
    right1 = new CANSparkMax(Constants.RIGHT_DRIVE_1_CAN_ID, MotorType.kBrushless);
    motorConfig(right1);
    right2 = new CANSparkMax(Constants.RIGHT_DRIVE_2_CAN_ID, MotorType.kBrushless);
    motorConfig(right2);
    right3 = new CANSparkMax(Constants.RIGHT_DRIVE_3_CAN_ID, MotorType.kBrushless);
    motorConfig(right3);

    // Create and configure the drive from the motors.
    leftGroup = new MotorControllerGroup(left1, left2, left3);
    rightGroup = new MotorControllerGroup(right1, right2, right3);
    rightGroup.setInverted(true);
    diffDrive = new DifferentialDrive(leftGroup, rightGroup);

    leftNeoEncoder = left1.getEncoder();
    leftEncoder = new Encoder(
        Constants.LEFT_DRIVE_A_CHANNEL,
        Constants.LEFT_DRIVE_B_CHANNEL,
        false,
        EncodingType.k4X);
    leftEncoder.setDistancePerPulse(Constants.DRIVE_INCHES_PER_PULSE);
    leftEncoder.setSamplesToAverage(5);
    rightEncoder = new Encoder(
        Constants.RIGHT_DRIVE_A_CHANNEL,
        Constants.RIGHT_DRIVE_B_CHANNEL,
        true, // TODO verify this.
        EncodingType.k4X);
    rightEncoder.setDistancePerPulse(Constants.DRIVE_INCHES_PER_PULSE);
    rightEncoder.setSamplesToAverage(5);

    // Create and configure sidewinder elements.
    sidewinderSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.SIDEWINDER_PCM_CHANNEL);
    sidewinderMotor = new WPI_TalonFX(Constants.SIDEWINDER_MOTOR_CAN_ID);
    sidewinderMotor.configFactoryDefault();
    // sidewinderMotor.configOpenloopRamp(0.5);
    // TODO other sidewinder motor config?
    this.sidewinderOverride = sidewinderOverride;

    this.navx = new AHRS(SPI.Port.kMXP);
    this.speedLimit = speedLimit;

    /*
    SmartDashboard.putNumber("1: ", left1.getAppliedOutput());
    SmartDashboard.putNumber("2: ", left2.getMotorTemperature());
    SmartDashboard.putNumber("3: ", left3.getOutputCurrent());
    SmartDashboard.putNumber("4: ", right1.getOutputCurrent());
    SmartDashboard.putNumber("5: ", right2.getOutputCurrent());
    SmartDashboard.putNumber("6: ", right3.getOutputCurrent());
    */

    //SmartDashboard.putNumber("Left Encoder: ", right);
    //SmartDashboard.putNumber("Right Encoder: ", right);

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    //SmartDashboard.putNumber("Distance from Encoders: ", this.getDistance());
    //SmartDashboard.putNumber("Left Encoder: ", this.getDistance());
    SmartDashboard.putNumber("angle: ", this.getHeading());
    //SmartDashboard.putNumber("Right Encoder: ", this.rightEncoder.getDistance());
    
  }

  /**
   * @param speed    the raw speed input.
   * @param rotation the raw rotaion input.
   * @param sidewind the sidewinder input. Only used if sidewinder is engaged.
   */
  public void arcadeDrive(double speed, final double rotation, final double sidewind) {

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
        if (Math.abs(getHeading()) > 180) { resetYaw(); }
        resetHeading();
      }
      
      this.sidewinderSolenoid.set(true);
      Lights.getInstance().setColor(LEDMode.TWINKLES);
    } else if (Math.abs(sidewind) < Constants.SIDEWINDER_DISABLE_THRESHOLD) {
      this.sidewinderSolenoid.set(false);
    }

    if (this.sidewinderSolenoid.get()) {
      sidewinderMotor.set(
          ControlMode.PercentOutput,
          -(sidewind - (Math.signum(sidewind) * Constants.SIDEWINDER_OUTPUT_OFFSET)));
      
      if(rotation == 0.0){
        if (lastRotation != 0.0) { 
          // Sidewinder has stopped swerving, but is still sidewinding
          if (Math.abs(getHeading()) > 180) { resetYaw(); }
          resetHeading();
        }

        arcadeRotation = strafeHorizonatal.calculate(getHeading() % 360, 0.0);
      }
      
      
    }

    lastRotation = rotation;
    diffDrive.arcadeDrive(arcadeSpeed, arcadeRotation, true);
  }

  /**
   * Stops all drive motors.
   */
  public void stop() {
    arcadeDrive(0.0, 0.0, 0.0);
  }

  /**
   * Resets the distance sensors (aka encoders) of the drive train.
   */
  public void resetDistance() {
    leftNeoEncoder.setPosition(0.0);
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
  public double getDistance() {
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
  public double getRate() {
    //return (this.leftEncoder.getRate() + this.rightEncoder.getRate()) / 2.0;
    //return this.leftEncoder.getRate();
    return leftNeoEncoder.getVelocity() / 8.68 * 19.24 / 60.0;
  }

  /**
   * Configures the getHeading method to return 0. For time limited robot oriented
   * movements, prefer this method over resetYaw.
   */
  public void resetHeading() {
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
  public double getHeading() {
    return this.navx.getAngle();
  }

  /**
   * Resets the robot's yaw to 0.
   * 
   * <p>
   * WARNING: Use this only in extreme cases. Sometimes the reset can take time
   * and getYaw could return non-zero values for a short time after this is
   * called. For time limited robot oriented movements, prefer resetHeading and
   * getHeading.
   * </p>
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
  public float getYaw() {
    return this.navx.getYaw();
  }

  /**
   * Configures a motor during construction.
   * 
   * @param motor the motor to configure.
   */
  private void motorConfig(final CANSparkMax motor) {
    motor.restoreFactoryDefaults();
    motor.setIdleMode(IdleMode.kBrake);
    motor.setOpenLoopRampRate(0.25);
    // TODO determine and set current limit.
  }
}
