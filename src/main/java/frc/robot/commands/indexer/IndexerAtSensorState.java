package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Indexer;

public class IndexerAtSensorState extends CommandBase {

  private static IndexerAtSensorState INSTANCE;
  private final Indexer indexer;
  private long startTime;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerAtSensorState getInstance (final Indexer indexer) {

    if (INSTANCE == null) {

      INSTANCE = new IndexerAtSensorState(indexer);
    }

    return INSTANCE;
  }

  private IndexerAtSensorState (final Indexer indexer) {

    this.indexer = indexer;
    this.addRequirements(this.indexer);
  }

  @Override
  public void initialize () {

    this.indexer.setToReceiveVelocity();
    this.startTime = System.currentTimeMillis();
  }

  @Override
  public boolean isFinished () {

    return System.currentTimeMillis() - this.startTime >= Constants.INDEXER_AT_SENSOR_TIME_ADJUSTMENT_MS;
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.indexer.isManualMode()) {

      PostLoopCommandScheduler.addCommandToSchedule(IndexerReadyToShootState.getInstance(this.indexer));
    }
  }
}
