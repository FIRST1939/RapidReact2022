// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;

/**
 * This command implements the indexer state "Ready To Shoot". There MUST be a
 * cargo present at the sensor before this command is scheduled. This command
 * will stop the movement of the cargo. It would be fine if the movement has
 * already been stopped, in fact, that would probably be a good idea.
 * 
 * <p>
 * Note that the execute and isFinished methods are omitted. The default
 * implementations in the commmand interface are what we need, nothing for
 * execute and always false for isFinished. This command only ends via
 * interruption by shooting or ejecting.
 * </p>
 */
public class IndexerReadyToShootState extends CommandBase {
  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerReadyToShootState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerReadyToShootState getInstance(final Indexer indexer) {
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
  private IndexerReadyToShootState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(this.indexer);
  }

  /**
   * Sets the mechanism to the state represented by this command. That is, the
   * indexer stops moving the cargo that is at sensor.
   */
  @Override
  public void initialize() {
    this.indexer.stop();
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
    IndexerShootingState.getInstance(this.indexer).schedule();
  }
}
