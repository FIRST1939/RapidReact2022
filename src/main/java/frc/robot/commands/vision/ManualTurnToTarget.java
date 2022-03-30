// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.subsystems.DriveTrain;

public class ManualTurnToTarget extends CommandBase {

  private final DriveTrain driveTrain;
  private final Limelight limelight;

  private double angle;
  private int direction = 0;
  private int pipeline;

  public ManualTurnToTarget(final DriveTrain driveTrain, final Limelight limelight, final int pipeline) {
    this.driveTrain = driveTrain;
    this.limelight = limelight;

    this.pipeline = pipeline;

    addRequirements(driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.limelight.setPipeline(pipeline);
    if(pipeline == Constants.SHOOTER_SHORT_PIPELINE){
      limelight.setIsClosePipeline(true);
    } else {
      limelight.setIsClosePipeline(false);
    }
    this.angle = limelight.getHorizontalAngleError();
    
    if (this.angle < -1) {
      this.direction = -1;
    } else if (angle > 1) {
      this.direction = 1;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.angle = limelight.getHorizontalAngleError();

    if (Math.abs(this.angle) > 60) {
      this.driveTrain.arcadeDrive(0, 0.70 * this.direction, 0);
    } else if (Math.abs(this.angle) > 30) {
      this.driveTrain.arcadeDrive(0, 0.55 * this.direction, 0);
    } else {
      this.driveTrain.arcadeDrive(0, 0.45 * this.direction, 0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.arcadeDrive(0, 0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (this.angle < 1 && this.angle > -1);
  }
}