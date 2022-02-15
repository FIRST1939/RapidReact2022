// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.RobotCargoCount;

/**
 * This command implements the indexer state "Shooting".
 * 
 * <p>
 * Note that the initialize method is omitted. The default implementations in
 * the commmand interface is what we need, nothing for initialize.
 * </p>
 */
public class IndexerShootingState extends CommandBase {
  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerShootingState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerShootingState getInstance(final Indexer indexer) {
    if (INSTANCE == null) {
      INSTANCE = new IndexerShootingState(indexer);
    }
    return INSTANCE;
  }

  /**
   * @param indexer the indexer subsystem to use.
   */
  private IndexerShootingState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(this.indexer);
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
    return !this.indexer.isCargoAtSensor();
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
    RobotCargoCount.getInstance().decrement();
    IndexerEmptyState.getInstance(this.indexer).schedule();
  }
}
