package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Indexer;

/**
 * This command implements the indexer state "Receiving."
 * 
 * Note that the initialize method is omitted. The default implementations in
 * the commmand interface is what we need, nothing for initialize.
 */
public class IndexerReceivingState extends CommandBase {

  private static IndexerReceivingState INSTANCE;
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerReceivingState getInstance (final Indexer indexer) {

    if (INSTANCE == null) {

      INSTANCE = new IndexerReceivingState(indexer);
    }

    return INSTANCE;
  }

  /**
   * @param indexer the indexer subsystem to use.
   */
  private IndexerReceivingState (final Indexer indexer) {

    this.indexer = indexer;
    this.addRequirements(indexer);
  }

  /**
   * This implementation makes sure we maintain the proper receive cargo speed.
   */
  @Override
  public void execute () {

    this.indexer.setToReceiveVelocity();
  }

  /**
   * This command ends normally when the cargo arrives at the sensor.
   */
  @Override
  public boolean isFinished () {

    return this.indexer.isCargoAtSensor();
  }

  /**
   * This implementation schedules the next state in the indexer state machine.
   * RobotCargoCount is incremented to keep track of cargo in robot.
   */
  @Override
  public void end (boolean interrupted) {

    this.indexer.stop();
    if (!this.indexer.isManualMode()) {

      PostLoopCommandScheduler.addCommandToSchedule(IndexerAtSensorState.getInstance(this.indexer));
    }
  }
}