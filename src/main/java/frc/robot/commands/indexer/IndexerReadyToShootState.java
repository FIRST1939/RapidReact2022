package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LEDMode;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Lights;

/**
 * This command implements the indexer state "Ready To Shoot". There MUST be a
 * cargo present at the sensor before this command is scheduled. This command
 * will stop the movement of the cargo. It would be fine if the movement has
 * already been stopped, in fact, that would probably be a good idea.
 * 
 * Note that the execute and isFinished methods are omitted. The default
 * implementations in the commmand interface are what we need, nothing for
 * execute and always false for isFinished. This command only ends via
 * interruption by shooting or ejecting.
 */
public class IndexerReadyToShootState extends CommandBase {

  private static IndexerReadyToShootState INSTANCE;
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerReadyToShootState getInstance (final Indexer indexer) {

    if (INSTANCE == null) {

      INSTANCE = new IndexerReadyToShootState(indexer);
    }

    return INSTANCE;
  }

  /**
   * Private in order to enforce singleton pattern.
   * 
   * @param indexer the indexer subsystem to use.
   */
  private IndexerReadyToShootState (final Indexer indexer) {

    this.indexer = indexer;
    this.addRequirements(this.indexer);
  }

  /**
   * Sets the mechanism to the state represented by this command. That is, the
   * indexer stops moving the cargo that is at sensor.
   */
  @Override
  public void initialize () {

    Lights.getInstance().setColor(LEDMode.RAINBOW);
    this.indexer.stop();
  }

  /**
   * This implementation schedules the next state in the indexer state machine.
   */
  @Override
  public void end (boolean interrupted) {
    
    // The only way this command ends is being interrupted by the
    // IndexerShootingState being scheduled, so this scheduling is
    // not needed and could cause issues when shooting quickly.
    /*
    if (!this.indexer.isManualMode()) {
      PostLoopCommandScheduler.addCommandToSchedule(
          IndexerShootingState.getInstance(indexer));
    }
    */
  }
}
