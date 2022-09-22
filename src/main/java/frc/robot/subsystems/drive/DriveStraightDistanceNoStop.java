// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

/**
 * This is the same as {@link DriveStraightDistance} except that the drive
 * motors are left on after completion.
 * 
 * <p>
 * WARNING: Use this with care and never as the last drive of an auto routine.
 */
public class DriveStraightDistanceNoStop extends DriveStraightDistance {
  /**
   * @param inches     the inches to drive. The intake end is forward (positive).
   *                   Pass a negative value for backwards.
   * @param driveTrain the drive train being controlled.
   * @param power      the forward or backward power for the motion [-1.0, 1.0].
   */
  public DriveStraightDistanceNoStop(final double inches, final DriveTrain driveTrain, final double power) {
    super(inches, driveTrain, power);
  }

  /**
   * WARNING: Leaves the drive train motors running.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
  }
}
