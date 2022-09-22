// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.devices.Targeting;
import frc.robot.subsystems.shooter.Shots;

/**
 * This command can only be effectively used after running {@link TurnToTarget}
 * to square up on the target.
 * 
 * <p>
 * This command will use the vertical angle to the target to move approximately
 * to the {@link Shots#cargoRing} shooting distance.
 */
public class MoveToCargoRing extends CommandBase {
  /** The drive train use to move the robot to the cargo ring. */
  private final DriveTrain driveTrain;
  /** The targeting device being used for the vertical angle. */
  private final Targeting targeting;
  /** The latest acquired vertical angle to the target. */
  private double ty;
  /** The direction to move (toward or away from the target). */
  private int direction = 0;

  /**
   * @param driveTrain the drive train use to move the robot to the cargo ring.
   * @param targeting  the targeting device being used for the vertical angle.
   */
  public MoveToCargoRing(final DriveTrain driveTrain, final Targeting targeting) {
    this.driveTrain = driveTrain;
    this.targeting = targeting;
    addRequirements(driveTrain);
  }

  /**
   * Read the {@link Targeting} device to decide if we should move forward or
   * backward to get to the proper cargo ring shooting distance.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    this.ty = this.targeting.getVerticalAngleError();

    if (this.ty < 6) {
      this.direction = -1;
    } else if (ty > 8) {
      this.direction = 1;
    } else {
      // Commands can run more than once. Always fully initialize.
      this.direction = 0;
    }
  }

  /**
   * Read the {@link Targeting} device to manage the speed of the movement.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    this.ty = this.targeting.getVerticalAngleError();

    if (Math.abs(this.ty) > 10) {
      this.driveTrain.arcadeDrive(0.45 * this.direction, 0, 0);
    } else if (Math.abs(this.ty) > 8) {
      this.driveTrain.arcadeDrive(0.4 * this.direction, 0, 0);
    } else {
      this.driveTrain.arcadeDrive(0.35 * this.direction, 0, 0);
    }
  }

  /**
   * Turn off the drive train after movement completion.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.arcadeDrive(0, 0, 0);
  }

  /**
   * Read the most recent {@link Targeting} device read in {@link #execute()} is
   * in range, we are finished.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean isFinished() {
    return (this.ty < 8 && this.ty > 6);
  }
}