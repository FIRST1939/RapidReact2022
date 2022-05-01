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
   * 
   * <p>
   * Note: I would have liked to make each state's command a member of the enum
   * itself, but that would involve non-static references from a static context,
   * which is not possible.
   */
  enum State {
    STOWED_EMPTY,
    GATHERING_EMPTY,
    AT_SENSOR,
    GATHERING_SEND,
    STOWED_HOLD,
    STOWED_SEND;

    private static State values[] = values();

    /**
     * Return the enumerator with the given ordinal. If the ordinal is out of range,
     * null is returned.
     * 
     * @param ordinal the ordinal of the enumerator being requested.
     * @return the enumerator or null if the ordinal is out of range.
     */
    static State getState(final int ordinal) {
      if (ordinal >= 0 && ordinal < values.length) {
        return values[ordinal];
      }
      return null;
    }
  }

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
   * null value is valid ({@link State#STOWED_EMPTY} will be used). This field is
   * cleared when use to start an initial state.
   */
  private final AtomicReference<State> nextInitialState = new AtomicReference<>();

  IntakeStateMachine(final Intake intake) {
    this.intake = intake;

    // Create the state commands.
    final Command stowedEmptyStateCommand = new InstantCommand(() -> {
      intake.retractIntake();
      intake.stopIntakeMotor();
    }, intake)
        .andThen(new WaitUntilCommand(intake::isExtensionRequested));
    final Command gatheringEmptyStateCommand = new FunctionalCommand(
        () -> {
          intake.extendIntake();
          Lights.getInstance().setColor(LEDMode.PINK);
        },
        intake::setIntakeSpeed,
        interrupted -> intake.stopIntakeMotor(),
        this::gatheringEmptyIsFinished,
        intake);
    final Command atSensorStateCommand = new RunCommand(() -> intake.setIntakeSpeed(), intake)
        .withTimeout(0.0)
        .until(intake::isRetractionRequested);
    final Command gatheringSendStateCommand = new FunctionalCommand(
        intake::extendIntake,
        intake::setIntakeSpeed,
        Function.identity()::apply,
        () -> !intake.isCargoAtSensor() || intake.isRetractionRequested(),
        intake);
    final Command stowedHoldStateCommand = new InstantCommand(() -> {
      intake.retractIntake();
      intake.stopIntakeMotor();
      Lights.getInstance().setColor(LEDMode.STROBE);
    }, intake)
        .andThen(new WaitUntilCommand(() -> !RobotCargoCount.getInstance().isFull()));
    final Command stowedSendStateCommand = new IntakeStowedSendState(intake);

    // Populate the state map.
    stateMap.put(State.STOWED_EMPTY, stowedEmptyStateCommand);
    stateMap.put(State.GATHERING_EMPTY, gatheringEmptyStateCommand);
    stateMap.put(State.AT_SENSOR, atSensorStateCommand);
    stateMap.put(State.GATHERING_SEND, gatheringSendStateCommand);
    stateMap.put(State.STOWED_HOLD, stowedHoldStateCommand);
    stateMap.put(State.STOWED_SEND, stowedSendStateCommand);
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
    if (!this.stateMachineCommand.isCurrentCommandIndexInRange()) {
      State suggestedNextInitialState = this.nextInitialState.getAndSet(null);
      return suggestedNextInitialState == null
          ? State.STOWED_EMPTY.ordinal()
          : suggestedNextInitialState.ordinal();
    }

    State next = State.STOWED_EMPTY;
    final State currentState = State.getState(current);
    if (currentState != null) {
      switch (currentState) {
        case STOWED_EMPTY:
          if (intake.extensionHandled()) {
            next = State.GATHERING_EMPTY;
          }
          break;

        case GATHERING_EMPTY:
          if (intake.retractionHandled()) {
            next = State.STOWED_EMPTY;
          }
          next = State.AT_SENSOR;
          break;

        case AT_SENSOR:
          if (intake.retractionHandled()) {
            if (RobotCargoCount.getInstance().isFull()) {
              next = State.STOWED_HOLD;
            } else {
              next = State.STOWED_SEND;
            }
          } else if (RobotCargoCount.getInstance().isFull()) {
            next = State.STOWED_HOLD;
          } else {
            next = State.GATHERING_SEND;
          }
          break;

        case GATHERING_SEND:
          if (intake.retractionHandled()) {
            next = State.STOWED_SEND;
          } else {
            next = State.GATHERING_EMPTY;
          }
          break;

        case STOWED_HOLD:
          next = State.STOWED_SEND;
          break;

        case STOWED_SEND:
          next = State.STOWED_EMPTY;
          break;

        default:
          break;
      }
    }
    return next.ordinal();
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
   * @param nextInitialState the {@link State} to use the next time the state
   *                         machine starts up (start of auto, teleop, or after
   *                         manual command ends).
   */
  void setNextInitialState(final State nextInitialState) {
    this.nextInitialState.set(nextInitialState);
  }

  private boolean gatheringEmptyIsFinished() {
    boolean cargoDetected = this.intake.isCargoAtSensor();
    if (cargoDetected) {
      RobotCargoCount.getInstance().increment();
    }
    return cargoDetected || this.intake.isRetractionRequested();
  }
}
