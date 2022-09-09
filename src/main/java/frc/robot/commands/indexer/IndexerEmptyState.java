package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Indexer;

public class IndexerEmptyState extends CommandBase {

  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerEmptyState INSTANCE;
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerEmptyState getInstance (final Indexer indexer) {

    if (INSTANCE == null) {

      INSTANCE = new IndexerEmptyState(indexer);
    }

    return INSTANCE;
  }

  private IndexerEmptyState (final Indexer indexer) {

    this.indexer = indexer;
    this.addRequirements(this.indexer);
  }

  @Override
  public void initialize () {

    this.indexer.stop();
  }

  @Override
  public boolean isFinished () {

    return this.indexer.isPriorStageSending();
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.indexer.isManualMode()) {

      PostLoopCommandScheduler.addCommandToSchedule(IndexerReceivingState.getInstance(this.indexer));
    }
  }
}
