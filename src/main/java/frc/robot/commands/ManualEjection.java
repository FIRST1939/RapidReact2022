// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.indexer.ManualEjectIndexer;
import frc.robot.commands.intake.ManualEjectIntake;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;

/**
 * This command is an explicit subclass of ParallelCommandGroup in order to
 * extend the initialize and end functionality.
 */
public class ManualEjection extends ParallelCommandGroup {
  final Intake intake;
  final Indexer indexer;

  /** Creates a new ManualEjection. */
  public ManualEjection(final Intake intake, final Indexer indexer) {
    this.intake = intake;
    this.indexer = indexer;
    addCommands(new ManualEjectIntake(this.intake), new ManualEjectIndexer(this.indexer));
  }

  @Override
  public void initialize() {
    this.intake.setManualMode(true);
    super.initialize();
  }

  @Override
  public void end(boolean interrupted) {
    this.intake.setManualMode(false);
    super.end(interrupted);
  }
}
