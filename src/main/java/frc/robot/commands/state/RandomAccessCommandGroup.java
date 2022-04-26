// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntUnaryOperator;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * A CommandGroup that runs a list of commands in an arbitray order.
 *
 * <p>
 * As a rule, CommandGroups require the union of the requirements of their
 * component commands.
 *
 * <p>
 * This class is provided by the NewCommands VendorDep TODO
 */
public class RandomAccessCommandGroup extends CommandGroupBase {
  /** The result of {@link #getCurrentCommandIndex()} if no current command. */
  public static final int NO_CURRENT_COMMAND = -1;

  private final List<Command> m_commands = new ArrayList<>();
  private final List<Command> m_unmodifiableCommands = Collections.unmodifiableList(m_commands);
  private final IntUnaryOperator nextCommandIndexOperator;
  private int m_currentCommandIndex = NO_CURRENT_COMMAND;
  private boolean m_runWhenDisabled = true;

  /**
   * Same as {@link #RandomAccessCommandGroup(IntUnaryOperator, Command...)} with
   * a null operator.
   *
   * @param commands the commands to include in this group.
   */
  public RandomAccessCommandGroup(Command... commands) {
    this(null, commands);
  }

  /**
   * Creates a new RandomAccessCommandGroup. The given commands will be run
   * in a arbitrary order, with the CommandGroup finishing when no command is
   * selected to run next.
   * 
   * <p>
   * Typically, if the nextCommandIndexOperator returned index is out of range
   * (see {@link #isCurrentCommandIndexInRange()}), this command group ends.
   * However, this command is designed to work properly with
   * {@link PerpetualCommand} (see {@link #perpetually()}). In that case, it will
   * not end, but will call the nextCommandIndexOperator during each execute call
   * with a parameter of -1.
   * 
   * <p>
   * The most common case for running this group perpetually is as a default
   * command for a subsystem and this group implements a state machine for the
   * subsystem.
   *
   * @param nextCommandIndexOperator an operator that takes the current index as a
   *                                 parameter and returns the next index. If
   *                                 null, an operator that simply increments the
   *                                 index is used. This results in behavior like
   *                                 a {@link SequentialCommandGroup}.
   * @param commands                 the commands to include in this group.
   */
  public RandomAccessCommandGroup(IntUnaryOperator nextCommandIndexOperator, Command... commands) {
    addCommands(commands);
    this.nextCommandIndexOperator = nextCommandIndexOperator != null ? nextCommandIndexOperator : i -> i + 1;
  }

  @Override
  public final void addCommands(Command... commands) {
    requireUngrouped(commands);

    if (m_currentCommandIndex != NO_CURRENT_COMMAND) {
      throw new IllegalStateException(
          "Commands cannot be added to a CommandGroup while the group is running");
    }

    // TODO unfortunately, this method is package scoped.
    // Put this back in if / when part of wpilib.
    // registerGroupedCommands(commands);

    for (Command command : commands) {
      m_commands.add(command);
      m_requirements.addAll(command.getRequirements());
      m_runWhenDisabled &= command.runsWhenDisabled();
    }
  }

  @Override
  public void initialize() {
    m_currentCommandIndex = getNextCommandIndex();

    if (isCurrentCommandIndexInRange()) {
      m_commands.get(m_currentCommandIndex).initialize();
    }
  }

  @Override
  public void execute() {
    if (!isCurrentCommandIndexInRange()) {
      // Either initialize resulted in no command or the call later in this method
      // resulted in no command and this group is being run perpetually.
      m_currentCommandIndex = getNextCommandIndex();
      if (isCurrentCommandIndexInRange()) {
        m_commands.get(m_currentCommandIndex).initialize();
      }
      return;
    }

    Command currentCommand = m_commands.get(m_currentCommandIndex);

    currentCommand.execute();
    if (currentCommand.isFinished()) {
      currentCommand.end(false);
      m_currentCommandIndex = getNextCommandIndex();
      if (isCurrentCommandIndexInRange()) {
        m_commands.get(m_currentCommandIndex).initialize();
      }
    }
  }

  @Override
  public boolean isFinished() {
    return !isCurrentCommandIndexInRange();
  }

  @Override
  public void end(boolean interrupted) {
    if (interrupted && isCurrentCommandIndexInRange()) {
      m_commands.get(m_currentCommandIndex).end(true);
    }
    m_currentCommandIndex = NO_CURRENT_COMMAND;
  }

  @Override
  public boolean runsWhenDisabled() {
    return m_runWhenDisabled;
  }

  /**
   * @return the current command index. It will be set to
   *         {@link #NO_CURRENT_COMMAND} if out of range.
   */
  public final boolean isCurrentCommandIndexInRange() {
    boolean inRange = m_currentCommandIndex >= 0 && m_currentCommandIndex < m_commands.size();
    if (!inRange) {
      m_currentCommandIndex = NO_CURRENT_COMMAND;
    }
    return inRange;
  }

  /**
   * @return the next command index. It will have already been set in
   *         {@link #m_currentCommandIndex} and set to {@link #NO_CURRENT_COMMAND}
   *         if out of range.
   */
  private int getNextCommandIndex() {
    m_currentCommandIndex = nextCommandIndexOperator.applyAsInt(m_currentCommandIndex);
    isCurrentCommandIndexInRange();
    return m_currentCommandIndex;
  }

  /**
   * @return the current command index. If no command is currently running,
   *         {@link #NO_CURRENT_COMMAND} is returned.
   */
  public final int getCurrentCommandIndex() {
    isCurrentCommandIndexInRange();
    return m_currentCommandIndex;
  }

  /**
   * @return an unmodifiable wrapper around the list of commands in this group.
   */
  public final List<Command> getCommands() {
    return m_unmodifiableCommands;
  }
}
