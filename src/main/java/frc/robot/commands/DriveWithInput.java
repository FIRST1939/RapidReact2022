// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveTrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * This command is designed to be the default command for the
 * drive train subsystem. It is used to drive during teleop
 * with driver input.
 */
public class DriveWithInput extends CommandBase {
  private final DriveTrain driveTrain;
  private final DoubleSupplier speedSupplier;
  private final DoubleSupplier rotationSupplier;
  private final DoubleSupplier sidewindSupplier;

  /**
   * Creates the command to drive the robot with driver input.
   *
   * @param driveTrain              the drive train subsystem used by this
   *                                command.
   * @param speedSupplier           a supplier for changing speed input. Often
   *                                derived from joystick input but does not have
   *                                to be.
   * @param rotationSupplier        a supplier for changing rotational input.
   *                                Often derived from joystick input but does not
   *                                have to be.
   * @param sidewinderSpeedSupplier a supplier for changing sidewinder input.
   *                                Often derived from joystick input but does not
   *                                have to be.
   */
  public DriveWithInput(final DriveTrain driveTrain, final DoubleSupplier speedSupplier,
      final DoubleSupplier rotationSupplier, final DoubleSupplier sidewinderSpeedSupplier) {
    this.driveTrain = driveTrain;
    this.speedSupplier = speedSupplier;
    this.rotationSupplier = rotationSupplier;
    this.sidewindSupplier = sidewinderSpeedSupplier;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.driveTrain.arcadeDrive(
        this.speedSupplier.getAsDouble(),
        this.rotationSupplier.getAsDouble(),
        this.sidewindSupplier.getAsDouble());
  }
  
  /**
   * Driving with driver input never ends unless interrupted.
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.driveTrain.stop();
  }
}
