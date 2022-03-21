// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;

/**
 * This command was created to avoid a race condition between scheduling the
 * manual belt driving commands and the state commands being able to see the
 * transition to manual mode in their end methods. The state commands need to
 * know to not start the next state if going manual. The problem was that the
 * end on the cancelled state commmand would be called before the initialize of
 * the new manual command. Therefore, recording the manual mode from initialize
 * of the manual belt movement commands is too late. This command sets manual
 * mode without subsystem requirement (and thus does not cancel any running
 * state) and then starts the belt running commands which then interrupt any
 * running state.
 * 
 * <p>
 * In a match, we would only enter manual mode if the intake / indexer cargo
 * pipeline was not working due to sensor failure. Therefore, coming out of
 * manual mode in match is not an expected action. However, we need to program
 * for the eventuality and testing. This code assumes that we have emptied the
 * robot of cargo while in manual mode and will re-enter the automated state
 * machine with this assumption.
 * </p>
 */
public class ToggleIntakeIndexerManualMode extends CommandBase {
  private final Intake intake;
  private final Indexer indexer;
  private final Command intakeManualCommand;
  private final Command indexerManualCommand;

  /** Creates a new ToggleIntakeIndexerManualMode. */
  public ToggleIntakeIndexerManualMode(
      final Intake intake,
      final Indexer indexer,
      final Command intakeManualCommand,
      final Command indexerManualCommand) {
    this.intake = intake;
    this.indexer = indexer;
    this.intakeManualCommand = intakeManualCommand;
    this.indexerManualCommand = indexerManualCommand;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (this.intake.isManualMode()) {
      this.intake.setManualMode(false);
      this.indexer.setManualMode(false);
      this.intakeManualCommand.cancel();
      this.indexerManualCommand.cancel();
    } else {
      this.intake.setManualMode(true);
      this.indexer.setManualMode(true);
      this.intakeManualCommand.schedule();
      this.indexerManualCommand.schedule();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
