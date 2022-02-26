// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class ManualIntakeExtend extends CommandBase {
  private final Intake intake;

  /** Creates a new ManualIntakeExtend. */
  public ManualIntakeExtend(final Intake intake) {
    this.intake = intake;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.intake.extendIntake();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
