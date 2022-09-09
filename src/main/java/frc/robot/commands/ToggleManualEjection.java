package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;

/**
 * This command was created to avoid a race condition between scheduling the
 * manual ejection command and the state commands being able to see the
 * transition to manual mode in their end methods. The state commands need to
 * know to not start the next state if going manual.
 */
public class ToggleManualEjection extends CommandBase {

  private final Intake intake;
  private final Indexer indexer;
  private final ManualEjection ejectionCommand;

  public ToggleManualEjection (final Intake intake, final Indexer indexer) {

    this.intake = intake;
    this.indexer = indexer;
    this.ejectionCommand = new ManualEjection(this.intake, this.indexer);
  }

  @Override
  public void initialize () {

    // Some paranoia to make sure we do not exit regular manual mode.
    if (this.intake.isManualMode() && this.indexer.isManualMode() && intake.getCurrentCommand() == this.ejectionCommand && indexer.getCurrentCommand() == this.ejectionCommand) {
          
      this.intake.setManualMode(false);
      this.indexer.setManualMode(false);
      this.ejectionCommand.cancel();
    } else {

      this.intake.setManualMode(true);
      this.indexer.setManualMode(true);
      this.ejectionCommand.schedule();
    }
  }

  @Override
  public boolean isFinished () {

    return true;
  }
}
