// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.devices.Targeting;

/**
 * Use this command to turn the robot to face the hub target. This is useful for
 * pre-shot turns during auto and teleop.
 * 
 * <p>
 * TODO fix to handle overshoot in execute. Change to PID?
 */
public class TurnToTarget extends CommandBase {
  /** Turn to within this tolerance in degrees. */
  private static final double TOLERANCE_DEGREES = 1.00;
  /** The {@link DriveTrain} being turned. */
  private final DriveTrain driveTrain;
  /** The {@link Targeting} being used to track the hub. */
  private final Targeting targeting;
  /** The latest angle to hub in degrees read during execution. */
  private double angle;
  /** The initialization time determined turn direction. */
  private int direction = 0;

  /**
   * Creates a command to turn to to the hub target.
   * 
   * @param driveTrain the {@link DriveTrain} to turn.
   * @param targeting  the {@link Targeting} implemenation used to track the hub.
   */
  public TurnToTarget(final DriveTrain driveTrain, final Targeting targeting) {
    this.driveTrain = driveTrain;
    this.targeting = targeting;
    addRequirements(driveTrain);
  }

  /**
   * Read the horizontal angle to the hub to initialize the turn direction.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    this.angle = targeting.getHorizontalAngleError();

    if (this.angle < -1) {
      this.direction = -1;
    } else if (angle > 1) {
      this.direction = 1;
    } else {
      // Commands can run more than once. Always fully initialize.
      this.direction = 0;
    }
  }

  /**
   * Read the horizontal angle remaining to calculate a turn speed.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    this.angle = targeting.getHorizontalAngleError();

    if (Math.abs(this.angle) > 60) {
      this.driveTrain.arcadeDrive(0, 0.70 * this.direction, 0);
    } else if (Math.abs(this.angle) > 30) {
      this.driveTrain.arcadeDrive(0, 0.55 * this.direction, 0);
    } else {
      this.driveTrain.arcadeDrive(0, 0.45 * this.direction, 0);
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
    return (this.angle < TOLERANCE_DEGREES && this.angle > -TOLERANCE_DEGREES);
  }
}