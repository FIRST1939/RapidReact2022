// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.devices.Targeting;

public class MoveToCargoRing extends CommandBase {
  private final DriveTrain driveTrain;
  private final Targeting targeting;

  private double ty;
  private int direction = 0;

  public MoveToCargoRing(final DriveTrain driveTrain, final Targeting targeting) {
    this.driveTrain = driveTrain;
    this.targeting = targeting;
    addRequirements(driveTrain);
  }

  // Called when the command is initially scheduled.
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

  // Called every time the scheduler runs while the command is scheduled.
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

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.arcadeDrive(0, 0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (this.ty < 8 && this.ty > 6);
  }
}