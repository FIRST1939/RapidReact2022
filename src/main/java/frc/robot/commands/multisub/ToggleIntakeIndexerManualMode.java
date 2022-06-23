// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.multisub;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.intake.Intake;

/**
 * This command was created to ensure the both the intake and indexer move into
 * and out of manual mode together. Note that since we now have a single command
 * group that implements a state machine, we no longer need to track manual
 * mode. We can just check to see if the state machine (wrapped as perpetual and
 * set as the subsystem's default command) is running.
 * 
 * <p>
 * In a match, we would only enter manual mode if the intake / indexer cargo
 * pipeline was not working due to sensor failure. Therefore, coming out of
 * manual mode in match is not an expected action. However, we need to program
 * for the eventuality and testing. This code assumes that we have emptied the
 * robot of cargo while in manual mode and will re-enter the automated state
 * machine with this assumption.
 */
public class ToggleIntakeIndexerManualMode extends CommandBase {
  private final Intake intake;
  private final Command intakeManualCommand;
  private final Command indexerManualCommand;

  /**
   * Creates a new ToggleIntakeIndexerManualMode.
   * 
   * @param intake               the intake to check to see if the state machine
   *                             is running.
   * @param intakeManualCommand  the command to run the intake manually.
   * @param indexerManualCommand the command to run the indexer manually.
   */
  public ToggleIntakeIndexerManualMode(
      final Intake intake,
      final Command intakeManualCommand,
      final Command indexerManualCommand) {
    this.intake = intake;
    this.intakeManualCommand = intakeManualCommand;
    this.indexerManualCommand = indexerManualCommand;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * If the state machine is running, enter manual mode by scheduling the manual
   * mode commands (will cancel the state machine). If the state machine is not
   * running, cancel the manual mode commands (will schedule the subsystem default
   * command which is the state machine).
   */
  @Override
  public void initialize() {
    if (this.intake.isStateMachineRunning()) {
      this.intakeManualCommand.schedule();
      this.indexerManualCommand.schedule();
    } else {
      this.intakeManualCommand.cancel();
      this.indexerManualCommand.cancel();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This is effectively and instance command and terminates immediately.
   */
  @Override
  public boolean isFinished() {
    return true;
  }
}
