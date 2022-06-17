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
 * This command was created to avoid a race condition between scheduling the
 * manual ejection command and the state commands being able to see the
 * transition to manual mode in their end methods. The state commands need to
 * know to not start the next state if going manual.
 */
public class ToggleManualEjection extends CommandBase {
  private final Intake intake;
  private final Indexer indexer;
  private final Command ejectionCommand;

  /** Creates a new ToggleManualEjection. */
  public ToggleManualEjection(
      final Intake intake,
      final Indexer indexer) {
    this.intake = intake;
    this.indexer = indexer;
    this.ejectionCommand = new ParallelCommandGroup(new ManualEjectIntake(this.intake), new ManualEjectIndexer(this.indexer));
  }

  // Called when the command is initially scheduled.
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

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
