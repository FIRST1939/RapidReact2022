// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Limelight;
import frc.robot.Constants.LEDMode;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Lights;

public class ManualMoveToTarget extends CommandBase {

  private final DriveTrain driveTrain;
  private final Limelight limelight;

  private double ty;
  private int direction = 0;

  private int pipeline;

  private final RumbleController rumbleController;

  public ManualMoveToTarget(final DriveTrain driveTrain, final Limelight limelight, final int pipeline, final RumbleController rumbleController) {
    this.driveTrain = driveTrain;
    this.limelight = limelight;
    this.pipeline = pipeline;

    this.rumbleController = rumbleController;

    addRequirements(driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Lights.getInstance().setColor(LEDMode.RED);
    this.ty = limelight.getVerticalAngleError();
    this.limelight.setPipeline(pipeline);
    
    if (this.ty < 6) {
      this.direction = -1;
    } else if (ty > 8) {
      this.direction = 1;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.ty = limelight.getVerticalAngleError();

    if (Math.abs(this.ty) > 10) {
      this.driveTrain.arcadeDrive(0.45 * this.direction, 0, 0);
    } else if (Math.abs(this.ty) > 8) {
      this.driveTrain.arcadeDrive(0.4 * this.direction, 0, 0);
    } else {
      this.driveTrain.arcadeDrive(0.35 * this.direction, 0, 0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.arcadeDrive(0, 0, 0);
    Lights.getInstance().setColor(LEDMode.GREEN);
    if (this.rumbleController != null) {
      this.rumbleController.schedule();
  }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (this.ty < 8 && this.ty > 6);
  }
}