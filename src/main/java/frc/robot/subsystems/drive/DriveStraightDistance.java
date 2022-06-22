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
   */
  public DriveStraightDistance(final double inches, final DriveTrain driveTrain, final double power) {
    this.forward = inches >= 0.0;
    this.absInches = Math.abs(inches);
    this.power = -power;
    this.driveTrain = driveTrain;
    addRequirements(this.driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.driveTrain.resetDistance();
    this.driveTrain.resetHeading();
  }

  // Called every time the scheduler runs while the command is scheduled.
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

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(driveTrain.getDistance()) >= this.absInches;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.stop();
  }
}
