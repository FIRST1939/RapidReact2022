package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.indexer.ManualEjectIndexer;
import frc.robot.commands.intake.ManualEjectIntake;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;

public class ManualEjection extends ParallelCommandGroup {

  final Intake intake;
  final Indexer indexer;

  public ManualEjection (final Intake intake, final Indexer indexer) {

    this.intake = intake;
    this.indexer = indexer;

    this.addCommands(new ManualEjectIntake(this.intake), new ManualEjectIndexer(this.indexer));
  }

  @Override
  public void initialize () {

    this.intake.setManualMode(true);
    this.indexer.setManualMode(true);
    super.initialize();
  }

  @Override
  public void end (boolean interrupted) {

    this.indexer.setManualMode(false);
    this.intake.setManualMode(false);
    super.end(interrupted);
  }
}
