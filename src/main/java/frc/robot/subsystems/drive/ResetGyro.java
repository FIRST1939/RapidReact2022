// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 * Resets the robot's yaw to 0.
 * 
 * <p>
 * WARNING: Use this only in extreme cases. Sometimes the reset can take time
 * and getYaw could return non-zero values for a short time after this is
 * called. For time limited robot oriented movements, prefer resetHeading and
 * getHeading.
 * </p>
 * 
 * <p>
 * NOTE: Do not replace this with an {@link InstantCommand} so as to keep the
 * usage warnings in place.
 * </p>
 */
public class ResetGyro extends InstantCommand {
  private final DriveTrain driveTrain;

  /**
   * Creates the command for the given {@link DriveTrain}.
   * 
   * @param driveTrain the {@link DriveTrain} whose gyro is to be reset.
   */
  public ResetGyro(final DriveTrain driveTrain) {
    this.driveTrain = driveTrain;
  }

  /**
   * Resets the yaw and the commands ends.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    driveTrain.resetYaw();
  }
}
