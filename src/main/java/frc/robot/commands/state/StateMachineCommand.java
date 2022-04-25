// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.state;

import static edu.wpi.first.wpilibj.util.ErrorMessages.requireNonNullParam;

import java.util.Map;
import java.util.function.Function;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class StateMachineCommand<S> extends CommandBase {
  private static final Command NO_COMMAND = new PrintCommand(
      "StateMachineCommand selector value does not correspond to any command!");
  private final Map<S, Command> m_commands;
  private final Function<StateMachineCommand<S>, S> m_selector;
  private boolean m_enabled = true;
  private S m_currentState = null;
  private Command m_currentCommand = null;

  /** Creates a new StateMachineCommand. */
  public StateMachineCommand(final Map<S, Command> commands, final Function<StateMachineCommand<S>, S> selector) {
    m_commands = requireNonNullParam(commands, "commands", "StateMachineCommand");
    m_selector = requireNonNullParam(selector, "selector", "StateMachineCommand");

    for (Command command : m_commands.values()) {
      m_requirements.addAll(command.getRequirements());
    }

    final Trigger rescheduleTrigger = new Trigger(() -> isEnabled() && !isScheduled());
    rescheduleTrigger.whenActive(this);
  }

  public boolean isEnabled() {
    return m_enabled;
  }

  public void setEnabled(boolean enabled) {
    m_enabled = enabled;
    if (!m_enabled) {
      m_currentState = null;
      m_currentCommand = null;
    }
  }

  public S getCurrentState() {
    return m_currentState;
  }

  public Command getCurrentCommand() {
    return m_currentCommand;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    final S nextState = m_selector.apply(this);
    if (nextState == null) {
      setEnabled(false);
    } else {
      m_currentState = nextState;
      m_currentCommand = m_commands.getOrDefault(nextState, NO_COMMAND);
    }
    if (isEnabled()) {
      m_currentCommand.initialize();
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (isEnabled()) {
      m_currentCommand.execute();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (m_currentCommand != null) {
      m_currentCommand.end(interrupted);
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !isEnabled() || m_currentCommand.isFinished();
  }

  @Override
  public boolean runsWhenDisabled() {
    boolean runsWhenDisabled = true;
    for (Command command : m_commands.values()) {
      runsWhenDisabled &= command.runsWhenDisabled();
    }
    return runsWhenDisabled;
  }
}
