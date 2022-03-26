// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Limelight;
import frc.robot.subsystems.DriveTrain;

public class ManualTurnToTarget extends CommandBase {

  private final DriveTrain driveTrain;
  private final Limelight limelight;

  double angle;

  public ManualTurnToTarget(final DriveTrain driveTrain, final Limelight limelight) {
    this.driveTrain = driveTrain;
    this.limelight = limelight;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    angle = limelight.getHorizontalAngleError();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(angle < -1){
      driveTrain.arcadeDrive(0, -0.4, 0);
    } else if (angle > 1){
      driveTrain.arcadeDrive(0, 0.4, 0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    driveTrain.arcadeDrive(0, 0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(angle < 1 && angle > -1){
      return true;
    } else {
      return false;
    }
  }
}
