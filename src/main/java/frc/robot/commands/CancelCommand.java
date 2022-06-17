// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.Set;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Cancels the given commands when this command is initialized. This command as
 * finishes instantly. This command is a generalization of the CancelClimb
 * command we use to have.
 */
public class CancelCommand extends CommandBase {
  private final Set<Command> m_toCancel;

  /**
   * Creates a new CancelCommand that cancels the given commands when initialized.
   *
   * @param toCancel the commands to cancel
   */
  public CancelCommand(Command... toCancel) {
    m_toCancel = Set.of(toCancel);
  }

  @Override
  public void initialize() {
    for (Command command : m_toCancel) {
      command.cancel();
    }
  }

  @Override
  public boolean isFinished() {
    return true;
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }
}
