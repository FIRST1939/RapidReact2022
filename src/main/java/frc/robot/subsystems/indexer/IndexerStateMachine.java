// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicReference;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.state.RandomAccessCommandGroup;
import frc.robot.subsystems.Lights;

/**
 * This class defines the state machine for automated operation of the indexer
 * including a {@link PerpetualCommand} wrapper around the
 * {@link RandomAccessCommandGroup} used to implement the state machine. The
 * wrapper can be used as the default command for the indexer subsystem.
 */
public class IndexerStateMachine {
  /**
   * This enumeration defines the state names for the machine. The order is
   * important for proper state transition.
   */
  public enum State {
    EMPTY,
    RECEIVING,
    AT_SENSOR,
    READY_TO_SHOOT,
    SHOOTING
  }

  /** The implementing command for {@link State#EMPTY} */
  private final Command emptyStateCommand;
  /** The implementing command for {@link State#RECEIVING} */
  private final Command receivingStateCommand;
  /** The implementing command for {@link State#AT_SENSOR} */
  private final Command atSensorStateCommand;
  /** The implementing command for {@link State#READY_TO_SHOOT} */
  private final Command readyToShootStateCommand;
  /** The implementing command for {@link State#SHOOTING} */
  private final Command shootingStateCommand;

  /**
   * An enumeration map of the state machine. Iteration order is the enum
   * declaration order. This is why passing the map values to the command group in
   * iteration order, along with enum ordinals as the index works.
   */
  private final EnumMap<State, Command> stateMap = new EnumMap<>(State.class);

  /** The state machine itself. */
  private final RandomAccessCommandGroup stateMachineCommand = new RandomAccessCommandGroup(
      this::getNextStateIndex,
      stateMap.values().toArray(new Command[stateMap.size()]));

  /**
   * A wrapper for default command setting that runs the machine perpetually. This
   * makes the machine satisfy the subsystem default command requirement that
   * isFinished always returns false.
   */
  private final PerpetualCommand defaultCommand = stateMachineCommand.perpetually();

  /**
   * Used to suggest the state the next time the transition is from no state. A
   * null value is valid and will use the first state as the initial state.
   */
  private final AtomicReference<State> nextInitialState = new AtomicReference<>();

  IndexerStateMachine(final Indexer indexer) {
    // Create the state commands.
    emptyStateCommand = new RunCommand(() -> indexer.stop(), indexer)
        .until(() -> indexer.isPriorStageSending());
    receivingStateCommand = new RunCommand(() -> indexer.setToReceiveVelocity(), indexer)
        .until(() -> indexer.isCargoAtSensor());
    atSensorStateCommand = new RunCommand(() -> indexer.setToShooterFeedVelocity(), indexer)
        .withTimeout(0.0);
    readyToShootStateCommand = new InstantCommand(
        () -> Lights.getInstance().setColor(LEDMode.RAINBOW), indexer)
            .andThen(new RunCommand(() -> indexer.stop(), indexer))
            .until(() -> indexer.fireShot());
    shootingStateCommand = new IndexerShootingState(indexer);

    // Populate the state map.
    stateMap.put(State.EMPTY, emptyStateCommand);
    stateMap.put(State.RECEIVING, receivingStateCommand);
    stateMap.put(State.AT_SENSOR, atSensorStateCommand);
    stateMap.put(State.READY_TO_SHOOT, readyToShootStateCommand);
    stateMap.put(State.SHOOTING, shootingStateCommand);

  }

  /**
   * This is the next command index operator for the state machine. For the
   * indexer, we just step through the machine in order and wrap back to 0 after
   * the last.
   * 
   * <p>
   * NOTE: If the current state is no state, we check to see if a next initial
   * state was set. If so, it is used rather than the state at index 0.
   * 
   * @param current the current state index as pass from the state machine
   *                command.
   */
  private int getNextStateIndex(final int current) {
    if (!this.stateMachineCommand.isCurrentCommandIndexInRange()) {
      State suggestedNextInitialState = this.nextInitialState.getAndSet(null);
      if (suggestedNextInitialState != null) {
        return suggestedNextInitialState.ordinal();
      }
    }

    int next = current + 1;
    if (next < 0 || next >= stateMap.size()) {
      next = 0;
    }
    return next;
  }

  /**
   * @return true if the state machine is running and false otherwise.
   */
  public boolean isStateMachineRunning() {
    return this.defaultCommand.isScheduled();
  }

  /**
   * @return the currently active {@link State} for the state machine. This will
   *         be null if the state machine is not running. It can be null if the
   *         running state machine is between states.
   */
  public State getCurrentState() {
    if (isStateMachineRunning() && this.stateMachineCommand.isCurrentCommandIndexInRange()) {
      return State.values()[this.stateMachineCommand.getCurrentCommandIndex()];
    }
    return null;
  }

  /**
   * @return the wrapper command appropriate for use as the default command.
   */
  public PerpetualCommand getDefaultCommand() {
    return this.defaultCommand;
  }

  public void setNextInitialState(State nextInitialState) {
    this.nextInitialState.set(nextInitialState);
  }
}
