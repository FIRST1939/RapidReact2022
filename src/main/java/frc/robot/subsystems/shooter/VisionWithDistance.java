// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import static frc.robot.Constants.Shooter.VISION_B;
import static frc.robot.Constants.Shooter.VISION_M;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LEDMode;
import frc.robot.Constants.Shots;
import frc.robot.devices.Lights;
import frc.robot.devices.Limelight;

public class VisionWithDistance extends CommandBase {

  private final Shooter shooter;
  private final Limelight limelight;

  private double dy;
  private double velocity;
  private double lastVelocitySentToShooter;

  public VisionWithDistance(final Shooter shooter, final Limelight limelight) {
    this.shooter = shooter;
    this.limelight = limelight;
  }

  @Override
  public void initialize() {
    this.lastVelocitySentToShooter = 0;
  }

  @Override
  public void execute() {
    if (this.shooter.getShot() == Shots.visionTracked) {
      if (this.limelight.isTargetFound()) {
        dy = this.limelight.getVerticalAngleError();
        velocity = dy * VISION_M + VISION_B;

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
      } else {
        Lights.getInstance().setColor(LEDMode.RED);
      }
    }
  }

  @Override
  public void end(boolean interrupted) {
    Lights.getInstance().setColor(LEDMode.CONFETTI);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return this.shooter.getShot() != Shots.visionTracked;
  }
}
