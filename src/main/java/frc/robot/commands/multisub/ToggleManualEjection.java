// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.multisub;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.manual.ManualEjectIndexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.manual.ManualEjectIntake;

/**
 * This command was created to ensure the both the intake and indexer move into
 * and out of manual ejection together. Note that since we now have a single
 * command group that implements a state machine, we no longer need to track
 * manual mode. We can just check to see if the state machine (wrapped as
 * perpetual and set as the subsystem's default command) is running.
 */
public class ToggleManualEjection extends CommandBase {
  private final Intake intake;
  private final Indexer indexer;
  private final Command ejectionCommand;

  /**
   * Creates a new ToggleManualEjection.
   * 
   * @param intake  the intake to check to see if the state machine is running.
   * @param indexer the indexer to check to see if the state machine is running.
   */
  public ToggleManualEjection(
      final Intake intake,
      final Indexer indexer) {
    this.intake = intake;
    this.indexer = indexer;
    this.ejectionCommand = new ParallelCommandGroup(
        new ManualEjectIntake(this.intake),
        new ManualEjectIndexer(this.indexer));
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * If the state machines are running, enter manual ejection by scheduling the
   * manual ejection command (will cancel the state machines). If the state
   * machines are not unning, cancel the manual ejection command (will schedule
   * the subsystem default commands which are the state machines).
   */
  @Override
  public void initialize() {
    // Some paranoia to make sure we do not exit regular manual mode.
    if (!this.intake.isStateMachineRunning()
        && !this.indexer.isStateMachineRunning()
        && intake.getCurrentCommand() == this.ejectionCommand
        && indexer.getCurrentCommand() == this.ejectionCommand) {
      this.ejectionCommand.cancel();
    } else {
      this.ejectionCommand.schedule();
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
