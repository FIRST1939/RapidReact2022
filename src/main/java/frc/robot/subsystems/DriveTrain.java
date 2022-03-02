// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * The drive train consists of three Neos on each side. Encoders are used for
 * path following in autonomous, as is a NavX gyro. Two line sensors are used to
 * stop at selected positions on the field.
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
  private final Encoder leftEncoder;
  private final Encoder rightEncoder;

  // Side winder related objects.
  private final Solenoid sidewinderSolenoid;
  private final WPI_TalonFX sidewinderMotor;

  /**
   * Creates a new drive train.
   */
  public DriveTrain() {
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
    sidewinderSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.SIDEWINDER_PCM_CHANNEL);
    sidewinderMotor = new WPI_TalonFX(Constants.SIDEWINDER_MOTOR_CAN_ID);
    sidewinderMotor.configFactoryDefault();
    // TODO other sidewinder motor config?
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * @param speed    the raw speed input.
   * @param rotation the raw rotaion input.
   * @param sidewind the sidewinder input. Only used if sidewinder is engaged.
   */
  public void arcadeDrive(final double speed, final double rotation, final double sidewind) {
    diffDrive.arcadeDrive(speed, rotation, true);

    // Sidewind above threshold, disengage below, leave as is in gap.
    if (Math.abs(sidewind) > Constants.SIDEWINDER_ENABLE_THRESHOLD) {
      this.sidewinderSolenoid.set(true);
    } else if (Math.abs(sidewind) < Constants.SIDEWINDER_DISABLE_THRESHOLD) {
      this.sidewinderSolenoid.set(false);
    }

    if (this.sidewinderSolenoid.get()) {
      sidewinderMotor.set(
          ControlMode.PercentOutput,
          sidewind - (Math.signum(sidewind) * Constants.SIDEWINDER_OUTPUT_OFFSET));
    }
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
    return (this.leftEncoder.getDistance() + this.rightEncoder.getDistance()) / 2.0;
  }

  /**
   * Note that the returned inches per second are the average of the left and
   * right side sensors.
   * 
   * @return the rate of travel in inches per second.
   */
  public double getRate() {
    return (this.leftEncoder.getRate() + this.rightEncoder.getRate()) / 2.0;
  }

  /**
   * Configures a motor during construction.
   * 
   * @param motor the motor to configure.
   */
  private void motorConfig(final CANSparkMax motor) {
    motor.restoreFactoryDefaults();
    motor.setIdleMode(IdleMode.kBrake);
    // TODO determine and set current limit.
  }
}
