// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.Constants.LEDMode;
import frc.robot.Constants.SHOTS;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.Shooter;

public class VisionWithDistance extends CommandBase {

  private final Shooter shooter;
  private final Limelight limelight;

  private double dy;
  private double velocity;

  public VisionWithDistance(final Shooter shooter, final Limelight limelight) {
    this.shooter = shooter;
    this.limelight = limelight;
  }

  @Override
  public void execute() {
    if (this.shooter.getShot() == SHOTS.visionTracked) {
      if (this.limelight.isTargetFound()) {
        dy = this.limelight.getVerticalAngleError();
        // TODO put the proper velocity function in here.
        velocity = dy * Constants.VISION_M + Constants.VISION_B;
        this.shooter.cargoShot((int) velocity, true);
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
    return this.shooter.getShot() != SHOTS.visionTracked;
  }
}
