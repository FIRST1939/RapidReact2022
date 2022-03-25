// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.indexer.IndexerReadyToShootState;
import frc.robot.commands.indexer.IndexerShootingState;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.RobotCargoCount;
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
   * <p>
   * NOTE: The command group that defines the autonomous command MUST set the shot
   * as early as possible before this command runs.
   * </p>
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

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if ((shotsRemaining > 0)
        && (this.indexer.getCurrentCommand() instanceof IndexerReadyToShootState)
        && (this.shooter.isShooterReady())) {
      IndexerShootingState.getInstance(this.indexer).schedule();
      shotsRemaining--;
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !this.indexer.isCargoAtSensor() && (this.shotsRemaining <= 0);
  }
}
