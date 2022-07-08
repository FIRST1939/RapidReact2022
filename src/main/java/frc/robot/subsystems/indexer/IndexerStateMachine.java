// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import static frc.robot.Constants.IndexerConstants.INDEXER_AT_SENSOR_TIME_ADJUSTMENT_SEC;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.commands.state.RandomAccessCommandGroup;
import frc.robot.devices.RobotCargoCount;

/**
 * This class defines the state machine for automated operation of the indexer
 * including a {@link PerpetualCommand} wrapper around the
 * {@link RandomAccessCommandGroup} used to implement the state machine. The
 * wrapper can be used as the default command for the indexer subsystem.
 * 
 * <p>
 * Note that this class and its content is at most package scoped (never
 * public). It is to be used directly by only the indexer subsystem.
 */
class IndexerStateMachine {
  /** The indexer this state machine is operating. */
  private final Indexer indexer;

  /**
   * This enumeration defines the state names for the machine. The order is
   * important for proper state transition.
   * 
   * <p>
   * Note: I would have liked to make each state's command a member of the enum
   * itself, but that would involve non-static references from a static context,
   * which is not possible.
   */
  enum State {
    EMPTY,
    RECEIVING,
    AT_SENSOR,
    READY_TO_SHOOT,
    SHOOTING;

    /**
     * For enum/ordinal mapping and bounds checking, the values array is useful.
     * However, each call to the values() method creates a new array and we use it
     * quite often. Stash a single copy here and use it at all times as it will
     * never change.
     */
    private static final State VALUES[] = values();

    /**
     * Return the enumerator with the given ordinal. If the ordinal is out of range,
     * null is returned.
     * 
     * @param ordinal the ordinal of the enumerator being requested.
     * @return the enumerator or null if the ordinal is out of range.
     */
    private static State getState(final int ordinal) {
      if (ordinal >= 0 && ordinal < VALUES.length) {
        return VALUES[ordinal];
      }
      return null;
    }
  }

  /** The state machine implementation command group. */
  private final RandomAccessCommandGroup stateMachineCommand;

  /**
   * A wrapper for default command setting that runs the machine perpetually. This
   * makes the machine satisfy the subsystem default command requirement that
   * isFinished always returns false.
   */
  private final PerpetualCommand defaultCommand;

  /**
   * @param indexer the {@link Indexer} subsystem to be operated.
   */
  IndexerStateMachine(final Indexer indexer) {
    this.indexer = indexer;

    // Create the state commands.
    final Command emptyStateCommand = new RunCommand(() -> indexer.stop(), indexer)
        .until(() -> indexer.isPriorStageSending());
    final Command receivingStateCommand = new RunCommand(() -> indexer.setToReceiveVelocity(), indexer)
        .until(() -> indexer.isCargoAtSensor());
    final Command atSensorStateCommand = new RunCommand(() -> indexer.setToShooterFeedVelocity(), indexer)
        .withTimeout(INDEXER_AT_SENSOR_TIME_ADJUSTMENT_SEC);
    final Command readyToShootStateCommand = new RunCommand(() -> indexer.stop(), indexer)
        .until(() -> indexer.fireShot());
    final Command shootingStateCommand = new StartEndCommand(
        () -> indexer.setToShooterFeedVelocity(),
        () -> {
          indexer.stop();
          RobotCargoCount.getInstance().decrement();
          indexer.shotFired();
        }, indexer)
            .raceWith(
                new ConditionalCommand(
                    new WaitUntilCommand(() -> !indexer.isCargoAtSensor()),
                    new WaitCommand(0.5), // Cargo slipped down case.
                    () -> indexer.isCargoAtSensor()));

    /*
     * Create the state machine implementing command group. Make sure the commands
     * are in the same order as the states in the State enumeration.
     */
    stateMachineCommand = new RandomAccessCommandGroup(
        this::getNextStateIndex,
        emptyStateCommand,
        receivingStateCommand,
        atSensorStateCommand,
        readyToShootStateCommand,
        shootingStateCommand);

    defaultCommand = stateMachineCommand.perpetually();
  }

  /**
   * This is the next command index operator for the state machine. If the current
   * state is no state, the result is EMPTY (see
   * {@link #setNextInitialState(State)} for initial state options).
   * 
   * <p>
   * For the indexer, we just step through the machine in order and wrap back to 0
   * after the last.
   * 
   * @param current the current state index as passed from the state machine
   *                command.
   * 
   * @return the next state index in the range [0, State.SIZE).
   */
  private int getNextStateIndex(final int current) {
    int next = current + 1;
    if (next < 0 || next >= State.VALUES.length) {
      next = 0;
    }
    return next;
  }

  /**
   * @return the currently active {@link State} for the state machine. This will
   *         be null if the state machine is not running. It can be null if the
   *         running state machine is between states.
   */
  State getCurrentState() {
    if (this.indexer.isStateMachineRunning()) {
      return State.getState(this.stateMachineCommand.getCurrentCommandIndex());
    }
    return null;
  }

  /**
   * @return the wrapper command appropriate for use as the default command.
   */
  PerpetualCommand getDefaultCommand() {
    return this.defaultCommand;
  }

  /**
   * @param nextInitialState the {@link State} to use the next time the state
   *                         machine starts up (start of auto, teleop, or after
   *                         manual command ends).
   */
  void setNextInitialState(final State nextInitialState) {
    this.stateMachineCommand.setInitialCommandIndex(
        nextInitialState == null
            ? RandomAccessCommandGroup.NO_CURRENT_COMMAND
            : nextInitialState.ordinal());
  }
}
