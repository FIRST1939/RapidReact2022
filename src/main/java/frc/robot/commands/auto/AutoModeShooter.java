package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.indexer.IndexerReadyToShootState;
import frc.robot.commands.indexer.IndexerShootingState;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

/**
 * Used during autonomous mode to shoot 1 or 2 cargo when each is ready to be
 * shot and the shooter is spun up.
 */
public class AutoModeShooter extends CommandBase {

  private int shotsRemaining;
  private final Indexer indexer;
  private final Shooter shooter;

  /**
   * This command just monitors the indexer and triggers the shooter. It does not
   * change their states directly and therfore does not require them.
   * 
   * NOTE: The command group that defines the autonomous command MUST set the shot
   * as early as possible before this command runs.
   * 
   * @param shots   the number of cargo to fire. Will be forced to be less than or
   *                equal to the number of cargo in the robot.
   * @param indexer the indexer being monitored.
   * @param shooter the shooter that will be triggered.
   */
  public AutoModeShooter (final int shots, final Indexer indexer, final Shooter shooter) {

    this.shotsRemaining = shots;
    this.indexer = indexer;
    this.shooter = shooter;
  }

  @Override
  public void execute () {

    if ((this.shotsRemaining > 0) && this.indexer.isCargoAtSensor() && (this.indexer.getCurrentCommand() instanceof IndexerReadyToShootState) && (this.shooter.isShooterReady())) {

      IndexerShootingState.getInstance(this.indexer).schedule();
      this.shotsRemaining--;
    }
  }

  @Override
  public boolean isFinished () {
    
    return !this.indexer.isCargoAtSensor() && (this.shotsRemaining <= 0);
  }
}
