// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.indexer.Indexer;

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
   * <p>
   * NOTE: The command group that defines the autonomous command MUST set the shot
   * as early as possible before this command runs.
   * 
   * <p>
   * This command typically runs as part of automode code; however, it could also
   * be part of auto shooting during teleop.
   * 
   * @param shots   the number of cargo to fire. Will be forced to be less than or
   *                equal to the number of cargo in the robot.
   * @param indexer the indexer being monitored.
   * @param shooter the shooter that will be triggered.
   */
  public AutoModeShooter(final int shots, final Indexer indexer, final Shooter shooter) {
    this.shotsRemaining = shots;
    this.indexer = indexer;
    this.shooter = shooter;
  }

  /**
   * {@inheritDoc}
   * 
   * @return true when there are still shots to fire, one is at the indexer
   *         sensor, the indexer is ready, the shooter is ready, and a request to
   *         shoot is allowed.
   */
  @Override
  public void execute() {
    if ((shotsRemaining > 0)
        && this.indexer.isCargoAtSensor()
        && indexer.isReadyToShoot()
        && this.shooter.isShooterReady()
        && indexer.requestShot()) {
      shotsRemaining--;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @return true when the last cargo to shoot is gone.
   */
  @Override
  public boolean isFinished() {
    return !this.indexer.isCargoAtSensor() && (this.shotsRemaining <= 0);
  }
}
