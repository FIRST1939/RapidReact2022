// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurnToAngle extends CommandBase {
  private final DriveTrain driveTrain;
  private double angle;
  private double target;
  private int direction = 0;
  private double distance;

  public TurnToAngle(final DriveTrain driveTrain, final double target) {
    this.driveTrain = driveTrain;
    this.target = target;
    addRequirements(driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.driveTrain.resetHeading();
    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    if (this.distance < -1) {
      this.direction = -1;
    } else if (this.distance > 1) {
      this.direction = 1;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    if (Math.abs(this.distance) > 31) {
      this.driveTrain.arcadeDrive(0, 0.75 * this.direction, 0);
    } else if (Math.abs(this.distance) > 15) {
      this.driveTrain.arcadeDrive(0, 0.6 * this.direction, 0);
    } else {
      this.driveTrain.arcadeDrive(0, 0.48 * this.direction, 0);
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
    this.distance = this.target - this.angle;
    return (this.distance < .75 && this.distance > -0.75);
  }
}