// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.RobotCargoCount;

/**
 * This command implements the indexer state "Shooting".
 * 
 * <p>
 * Note that the initialize method is omitted. The default implementations in
 * the commmand interface is what we need, nothing for initialize.
 * </p>
 */
class IndexerShootingState extends CommandBase {
  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  /** Used to make sure this state runs for a minimum time. */
  private final Timer minRunTimer = new Timer();
  /** Have to have our own flag since stupid Timer does not have an isRunning() */
  private boolean minRunTimerEnabled = false;

  /**
   * @param indexer the indexer subsystem to use.
   */
  IndexerShootingState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(this.indexer);
  }

  @Override
  public void initialize() {
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
   * {@inheritDoc}
   * 
   * <p>
   * This implementation makes sure we maintain the proper feed cargo to shooter
   * speed.
   * </p>
   */
  @Override
  public void execute() {
    this.indexer.setToShooterFeedVelocity();
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This command ends normally when the cargo is no longer visible at the sensor.
   * </p>
   */
  @Override
  public boolean isFinished() {
    return !this.indexer.isCargoAtSensor()
        && (!this.minRunTimerEnabled || (this.minRunTimer.get() > 0.5)); // TODO validate time
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation schedules the next state in the indexer state machine.
   * </p>
   */
  @Override
  public void end(boolean interrupted) {
    this.indexer.stop();
    this.minRunTimer.stop();
    this.minRunTimerEnabled = false;
    RobotCargoCount.getInstance().decrement();
    this.indexer.shotFired();
  }
}
