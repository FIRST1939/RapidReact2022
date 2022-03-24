// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;

public class TurnToTargetTeleop extends CommandBase {
  /** Creates a new TurnToTargetTeleop. */

  private final DriveTrain drivetrain;
  private final Robot robot;

  PIDController turnToAngle = new PIDController(Constants.VISION_TURN_TO_ANGLE_kP, 0, 0);

  public TurnToTargetTeleop(final DriveTrain drivetrain, final Robot robot) {
    this.drivetrain = drivetrain;
    this.robot = robot;
  }

  @Override
  public void initialize() {
    this.drivetrain.resetHeading();
    turnToAngle.reset();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {;
    double angle = this.robot.limelightTurret.getHorizontalAngleError();
    double output = turnToAngle.calculate(angle);
    this.drivetrain.arcadeDrive(0, output, 0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(Math.abs(this.robot.limelightTurret.getHorizontalAngleError()) < Constants.VISION_ANGLE_ERROR){
      return true;
    } else {
      return false;
    }
  }
}
