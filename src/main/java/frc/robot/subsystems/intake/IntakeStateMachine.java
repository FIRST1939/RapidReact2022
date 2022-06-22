// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import static frc.robot.Constants.IntakeConstants.INTAKE_AT_SENSOR_TIME_ADJUSTMENT_SEC;

import java.util.function.Function;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.commands.state.EnumeratedRandomAccessCommandGroup;
import frc.robot.devices.RobotCargoCount;

/**
 * This class defines the state machine for automated operation of the intake
 * including a {@link PerpetualCommand} wrapper around the
 * {@link EnumeratedRandomAccessCommandGroup} used to implement the state
 * machine. The wrapper can be used as the default command for the intake
 * subsystem.
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

  /** The state machine itself. */
  private final EnumeratedRandomAccessCommandGroup<State> stateMachineCommand;

  /**
   * A wrapper for default command setting that runs the machine perpetually. This
   * makes the machine satisfy the subsystem default command requirement that
   * isFinished always returns false.
   */
  private final PerpetualCommand defaultCommand;

  IntakeStateMachine(final Intake intake) {
    this.intake = intake;

    // Create the state commands.
    final Command stowedEmptyStateCommand = new InstantCommand(() -> {
      intake.retractIntake();
      intake.stopIntakeMotor();
    }, intake)
        .andThen(new WaitUntilCommand(intake::isExtensionRequested));
    final Command gatheringEmptyStateCommand = new FunctionalCommand(
        intake::extendIntake,
        intake::setIntakeSpeed,
        interrupted -> intake.stopIntakeMotor(),
        this::gatheringEmptyIsFinished,
        intake);
    final Command atSensorStateCommand = new RunCommand(() -> intake.setIntakeSpeed(), intake)
        .withTimeout(INTAKE_AT_SENSOR_TIME_ADJUSTMENT_SEC)
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
    }, intake)
        .andThen(new WaitUntilCommand(() -> !RobotCargoCount.getInstance().isFull()));
    final Command stowedSendStateCommand = new IntakeStowedSendState(intake);

    /*
     * Create the state machine implementing command group. Make sure the commands
     * are in the same order as the states in the State enumeration.
     */
    stateMachineCommand = new EnumeratedRandomAccessCommandGroup<>(
        State.class,
        this::getNextStateIndex,
        stowedEmptyStateCommand,
        gatheringEmptyStateCommand,
        atSensorStateCommand,
        gatheringSendStateCommand,
        stowedHoldStateCommand,
        stowedSendStateCommand);

    defaultCommand = stateMachineCommand.perpetually();
  }

  /**
   * This is the next command state operator for the state machine. If the current
   * state is no state, the result is STOWED_EMPTY (see
   * {@link #setNextInitialState(State)} for initial state options).
   * 
   * @param currentState the current state index as passed from the state machine
   *                     command.
   */
  private State getNextStateIndex(final State currentState) {
    State next = State.STOWED_EMPTY;
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
    return next;
  }

  /**
   * @return the currently active {@link State} for the state machine. This will
   *         be null if the state machine is not running. It can be null if the
   *         running state machine is between states.
   */
  State getCurrentState() {
    return this.stateMachineCommand.getCurrentState();
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
    this.stateMachineCommand.setInitialState(nextInitialState);
  }

  /**
   * Implementation of {@link State#GATHERING_EMPTY} command's isFinished just to
   * keep the declaration in the constructor a bit more tidy.
   * 
   * @return true if the command is finished and false otherwise.
   */
  private boolean gatheringEmptyIsFinished() {
    boolean cargoDetected = this.intake.isCargoAtSensor();
    if (cargoDetected) {
      RobotCargoCount.getInstance().increment();
    }
    return cargoDetected || this.intake.isRetractionRequested();
  }
}
