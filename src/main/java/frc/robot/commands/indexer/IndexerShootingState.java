package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.RobotCargoCount;

/**
 * This command implements the indexer state "Shooting".
 * 
 * Note that the initialize method is omitted. The default implementations in
 * the commmand interface is what we need, nothing for initialize.
 */
public class IndexerShootingState extends CommandBase {

  private static IndexerShootingState INSTANCE;
  private final Indexer indexer;

  private final Timer minRunTimer = new Timer();
  private boolean minRunTimerEnabled = false;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerShootingState getInstance (final Indexer indexer) {

    if (INSTANCE == null) {

      INSTANCE = new IndexerShootingState(indexer);
    }

    return INSTANCE;
  }

  /**
   * @param indexer the indexer subsystem to use.
   */
  private IndexerShootingState (final Indexer indexer) {

    this.indexer = indexer;
    this.addRequirements(this.indexer);
  }

  @Override
  public void initialize () {

    System.out.println("Shooting State init");
    this.minRunTimer.stop();
    this.minRunTimerEnabled = false;

    // The cargo must have slipped down, ensure min run time.
    if (!this.indexer.isCargoAtSensor()) {

      this.minRunTimer.reset();
      this.minRunTimer.start();
      this.minRunTimerEnabled = true;
    }
  }

  /**
   * This implementation makes sure we maintain the proper feed cargo to shooter
   * speed.
   */
  @Override
  public void execute () {

    this.indexer.setToShooterFeedVelocity();
  }

  /**
   * This command ends normally when the cargo is no longer visible at the sensor.
   */
  @Override
  public boolean isFinished () {

    return !this.indexer.isCargoAtSensor() && (!this.minRunTimerEnabled || (this.minRunTimer.get() > 0.5)); // TODO Validate Time
  }

  /**
   * This implementation schedules the next state in the indexer state machine.
   */
  @Override
  public void end (boolean interrupted) {

    this.indexer.stop();
    this.minRunTimer.stop();
    this.minRunTimerEnabled = false;
    RobotCargoCount.getInstance().decrement();

    if (!this.indexer.isManualMode()) {

      PostLoopCommandScheduler.addCommandToSchedule(IndexerEmptyState.getInstance(this.indexer));
    }
  }
}
