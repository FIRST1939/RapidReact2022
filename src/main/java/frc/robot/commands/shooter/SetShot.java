// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Shooter;

public class SetShot extends CommandBase {
  private final Constants.Shots shotType;
  private final Shooter shooter;
  /** Creates a new setShot. */
  public SetShot(Constants.Shots shotType, Shooter shooter) {
    this.shotType = shotType;
    this.shooter = shooter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.shooter.setHood(shotType.hood);
    this.shooter.setVelocity(shotType.speed);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
