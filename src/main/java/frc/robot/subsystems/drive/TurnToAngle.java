// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Use this command to turn the robot to an angle relative to its current
 * orientation. This is useful for turns of a known angle during autonomous.
 * 
 * <p>
 * TODO fix to handle overshoot in execute. Change to PID?
 */
public class TurnToAngle extends CommandBase {
  /** Turn to within this tolerance in degrees. */
  private static final double TOLERANCE_DEGREES = 0.75;

  /** The {@link DriveTrain} being turned. */
  private final DriveTrain driveTrain;
  /** The latest heading in degrees read during execution. */
  private double angle;
  /** The target turn angle in degrees from construction. */
  private final double target;
  /** The initialization time determined turn direction. */
  private int direction = 0;
  /** The amount of turn remaining in degrees. */
  private double distance;

  /**
   * Creates a command to turn to a specific angle relative to the current
   * heading.
   * 
   * @param driveTrain the {@link DriveTrain} to turn.
   * @param target     the desired turn angle in degrees.
   */
  public TurnToAngle(final DriveTrain driveTrain, final double target) {
    this.driveTrain = driveTrain;
    this.target = target;
    addRequirements(driveTrain);
  }

  /**
   * Reset the heading to measure the turn from 0 and then calculate the direction
   * of the turn.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    this.driveTrain.resetHeading();
    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    if (this.distance < -1) {
      this.direction = -1;
    } else if (this.distance > 1) {
      this.direction = 1;
    }
  }

  /**
   * Calculate the distance remaining and set a turn speed relative to that angle.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    if (Math.abs(this.distance) > 31) {
      this.driveTrain.arcadeDrive(0, 0.75 * this.direction, 0);
    } else if (Math.abs(this.distance) > 15) {
      this.driveTrain.arcadeDrive(0, 0.6 * this.direction, 0);
    } else {
      this.driveTrain.arcadeDrive(0, 0.48 * this.direction, 0);
    }
  }

  /**
   * Turn off the drive train.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.arcadeDrive(0, 0, 0);
  }

  /**
   * {@inheritDoc}
   * 
   * @return true when the distance is within the tolerance to end the command.
   */
  @Override
  public boolean isFinished() {
    this.distance = this.target - this.angle;
    return (this.distance < TOLERANCE_DEGREES && this.distance > -TOLERANCE_DEGREES);
  }
}