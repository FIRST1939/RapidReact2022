// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.subsystems.Shooter;

public class VisionWithDistance extends CommandBase {

  private final Shooter shooter;
  private final Limelight limelight;

  private double dy;
  private double speed;

  public VisionWithDistance(final Shooter shooter, final Limelight limelight) {
    this.shooter = shooter;
    this.limelight = limelight;
  }

  @Override
  public void initialize() {
    dy = limelight.getVerticalAngleError();
  }

  @Override
  public void execute() {
    if(limelight.isClosePipeline()){
      speed = dy * Constants.SHOOTER_DY_MULTIPLIER_CLOSE;
    } else {
      speed = dy * Constants.SHOOTER_DY_MULTIPLIER_LONG;
    }

    this.shooter.cargoShot();
  }


  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
