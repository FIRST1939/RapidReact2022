// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.state.RandomAccessCommandGroup;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.RobotCargoCount;

/**
 * This class defines the state machine for automated operation of the intake
 * including a {@link PerpetualCommand} wrapper around the
 * {@link RandomAccessCommandGroup} used to implement the state machine. The
 * wrapper can be used as the default command for the intake subsystem.
 */
class IntakeStateMachine {
  /** The intake this state machine operates. */
  private final Intake intake;

  /**
   * This enumeration defines the state names for the machine. The order is
   * important for proper state transition.
   */
  enum State {
    STOWED_EMPTY,
    GATHERING_EMPTY,
    AT_SENSOR,
    GATHERING_SEND,
    STOWED_HOLD,
    STOWED_SEND;

    private static State values[] = values();

    static State getState(final int ordinal) {
      if (ordinal >= 0 && ordinal < values.length) {
        return values[ordinal];
      }
      return null;
    }
  }

  /** The implementing command for {@link State#STOWED_EMPTY} */
  private final Command stowedEmptyStateCommand;
  /** The implementing command for {@link State#GATHERING_EMPTY} */
  private final Command gatheringEmptyStateCommand;
  /** The implementing command for {@link State#AT_SENSOR} */
  private final Command atSensorStateCommand;
  /** The implementing command for {@link State#GATHERING_SEND} */
  private final Command gatheringSendStateCommand;
  /** The implementing command for {@link State#STOWED_HOLD} */
  private final Command stowedHoldStateCommand;
  /** The implementing command for {@link State#STOWED_SEND} */
  private final Command stowedSendStateCommand;

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

  private final Trigger stateMachineActivationTrigger;

  /**
   * A wrapper for default command setting that runs the machine perpetually. This
   * makes the machine satisfy the subsystem default command requirement that
   * isFinished always returns false.
   */
  private final PerpetualCommand defaultCommand = stateMachineCommand.perpetually();

  /**
   * Used to send the state machine to a specific state rather than the next
   * sequential state. If the state machine is not running, this state will be the
   * initial state when it restarts. This field is cleared on state machine
   * termination.
   */
  private final AtomicReference<State> forceToState = new AtomicReference<>();

  IntakeStateMachine(final Intake intake) {
    this.intake = intake;
    this.stateMachineActivationTrigger = new Trigger(this.intake::isStateMachineRunning);

    // Create the state commands.
    stowedEmptyStateCommand = new InstantCommand(() -> {
      intake.retractIntake();
      intake.stopIntakeMotor();
    }, intake)
        .andThen(new WaitUntilCommand(() -> isForceToState()));
    gatheringEmptyStateCommand = new FunctionalCommand(
        () -> {
          intake.extendIntake();
          Lights.getInstance().setColor(LEDMode.PINK);
        },
        intake::setIntakeSpeed,
        interrupted -> intake.stopIntakeMotor(),
        this::gatheringEmptyIsFinished,
        intake);
    atSensorStateCommand = new RunCommand(() -> intake.setIntakeSpeed(), intake)
        .withTimeout(0.0)
        .until(this::isForceToState);
    gatheringSendStateCommand = new FunctionalCommand(
        intake::extendIntake,
        intake::setIntakeSpeed,
        Function.identity()::apply,
        () -> !intake.isCargoAtSensor() || isForceToState(),
        intake);
    stowedHoldStateCommand = new InstantCommand(() -> {
      intake.retractIntake();
      intake.stopIntakeMotor();
      Lights.getInstance().setColor(LEDMode.STROBE);
    }, intake)
        .andThen(new WaitUntilCommand(() -> !RobotCargoCount.getInstance().isFull() || isForceToState()));
    stowedSendStateCommand = new IntakeStowedSendState(intake)
        .until(this::isForceToState);

    // Populate the state map.
    stateMap.put(State.STOWED_EMPTY, stowedEmptyStateCommand);
    stateMap.put(State.GATHERING_EMPTY, gatheringEmptyStateCommand);
    stateMap.put(State.AT_SENSOR, atSensorStateCommand);
    stateMap.put(State.GATHERING_SEND, gatheringSendStateCommand);
    stateMap.put(State.STOWED_HOLD, stowedHoldStateCommand);
    stateMap.put(State.STOWED_SEND, stowedSendStateCommand);

    // Clean up on state machine termination.
    this.stateMachineActivationTrigger.whenInactive(new InstantCommand(() -> forceToState(null)));
  }

  /**
   * This is the next command index operator for the state machine.
   * 
   * <p>
   * NOTE: If the current state is no state, we check to see if a next initial
   * state was set. If so, it is used rather than the state at index 0.
   * 
   * @param current the current state index as passed from the state machine
   *                command.
   */
  private int getNextStateIndex(final int current) {
    // Handle forcing to specific state.
    if (isForceToState()) {
      return this.forceToState.getAndSet(null).ordinal();
    }

    int next = State.STOWED_EMPTY.ordinal();
    final State currentState = State.getState(current);
    if (currentState != null) {
      switch (currentState) {
        case AT_SENSOR:
          // Note: stop intake case handled in requestRetraction via force
          if (RobotCargoCount.getInstance().isFull()) {
            next = State.STOWED_HOLD.ordinal();
          } else {
            next = State.GATHERING_SEND.ordinal();
          }
          break;

        default:
          break;
      }
    }
    return next;
  }

  /**
   * @return the currently active {@link State} for the state machine. This will
   *         be null if the state machine is not running. It can be null if the
   *         running state machine is between states.
   */
  State getCurrentState() {
    if (this.intake.isStateMachineRunning() && this.stateMachineCommand.isCurrentCommandIndexInRange()) {
      return State.values()[this.stateMachineCommand.getCurrentCommandIndex()];
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
   * Used to send the state machine to a specific state rather than the next
   * sequential state. If the state machine is not running, this state will be the
   * initial state when it restarts. This field is cleared on state machine
   * termination.
   * 
   * @param forceToState the state to enter.
   */
  void forceToState(State forceToState) {
    this.forceToState.set(forceToState);
  }

  boolean isForceToState() {
    return this.forceToState.get() != null;
  }

  private boolean gatheringEmptyIsFinished() {
    boolean cargoDetected = this.intake.isCargoAtSensor();
    if (cargoDetected) {
      RobotCargoCount.getInstance().increment();
      this.forceToState(State.AT_SENSOR);
    }
    return cargoDetected || this.isForceToState();
  }
}
