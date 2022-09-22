// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import static frc.robot.Constants.DriveTrainConstants.DRIVE_AUTO_GYRO_STRAIGHT_KP;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Use this command during autonomous to drive a straight line for a distance
 * passed to the constructor.
 */
public class DriveStraightDistance extends CommandBase {
  private final boolean forward;
  private final double absInches;
  private final DriveTrain driveTrain;

  private final double power;

  /**
   * @param inches     the inches to drive. The intake end is forward (positive).
   *                   Pass a negative value for backwards.
   * @param driveTrain the drive train being controlled.
   * @param power      the forward or backward power for the motion [-1.0, 1.0].
   */
  public DriveStraightDistance(final double inches, final DriveTrain driveTrain, final double power) {
    this.forward = inches >= 0.0;
    this.absInches = Math.abs(inches);
    this.power = -power;
    this.driveTrain = driveTrain;
    addRequirements(this.driveTrain);
  }

  /**
   * Resets the drive sensors in preparation for driving straight.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    this.driveTrain.resetDistance();
    this.driveTrain.resetHeading();
  }

  /**
   * Calculates a turn value based on the amount the heading has moved from 0.0.
   * This turn value is combined with the power provided to the contructor to
   * execute an arcade turn.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    double turningValue = (-this.driveTrain.getHeading()) * DRIVE_AUTO_GYRO_STRAIGHT_KP;
    // Invert the direction of the turn if we are going backwards
    if (forward) {
      turningValue = -turningValue;
    }

    this.driveTrain.arcadeDrive(
        forward ? power : -power,
        turningValue,
        0.0);
  }

  /**
   * {@inheritDoc}
   * 
   * @return true once the desired distance is traversed.
   */
  @Override
  public boolean isFinished() {
    return Math.abs(driveTrain.getDistance()) >= this.absInches;
  }

  /**
   * Stops the drive train motors.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.stop();
  }
}
