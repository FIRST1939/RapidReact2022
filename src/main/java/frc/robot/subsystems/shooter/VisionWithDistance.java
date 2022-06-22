// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import static frc.robot.Constants.Shooter.VISION_B;
import static frc.robot.Constants.Shooter.VISION_M;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.devices.Targeting;

/**
 * This command is designed to run when {@link Shots#visionTracked} is selected
 * for the {@link Shooter}. It tracks the vision target and using a linear
 * vertical angle to velocity equation, adjusts the shooter velocity for the
 * distance to the upper hub.
 * 
 * <p>
 * Note that a changed velocity is only sent to the shooter if it falls outside
 * of a narrow range around the last velocity sent to the shooter. This enables
 * the shooter to properly debounce the monitored shooter velocity and report
 * the shooter ready. See {@link Shooter#cargoShot(int, boolean)} and
 * {@link Shooter#periodic()}.
 */
public class VisionWithDistance extends CommandBase {
  private final Shooter shooter;
  private double lastVelocitySentToShooter;

  /**
   * Create a new command for the given shooter.
   * 
   * @param shooter the shooter to be managed by this command.
   */
  public VisionWithDistance(final Shooter shooter) {
    this.shooter = shooter;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Each time this command in scheduled, clear the last sent value so that the
   * first velocity calculated will be sent to the shooter.
   */
  @Override
  public void initialize() {
    this.lastVelocitySentToShooter = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * If still vision tracked and we have a valid target, calculate a new shooter
   * velocity.
   * 
   * <p>
   * Only send it to the shooter is if is sufficiently different from the last
   * calculated velocity that was sent. This enables the shooter to properly
   * debounce the monitored shooter velocity and report the shooter ready. See
   * {@link Shooter#cargoShot(int, boolean)} and {@link Shooter#periodic()}.
   */
  @Override
  public void execute() {
    if (this.shooter.getShot() == Shots.visionTracked) {
      final Targeting shooterTargeting = this.shooter.getTargeting();
      if (shooterTargeting.isTargetFound()) {
        double dy = shooterTargeting.getVerticalAngleError();
        double velocity = dy * VISION_M + VISION_B;

        // I have no idea if 2% change is the right amount of change
        // to trigger setting the velocity and restarting the periodic
        // check to see if the shooter is ready. But it is half the
        // current error checked in the shooter, so seemed like a
        // reasonable first try. If it still does not shoot, relax
        // this a bit more. Perhaps 3% or 4% (same as error tolerance).
        if (Math.abs(this.lastVelocitySentToShooter - velocity) > this.lastVelocitySentToShooter * 0.02) {
          this.shooter.cargoShot((int) velocity, true);
          this.lastVelocitySentToShooter = velocity;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This command ends when a different shot is selected.
   */
  @Override
  public boolean isFinished() {
    return this.shooter.getShot() != Shots.visionTracked;
  }
}
