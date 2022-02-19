// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;

/**
 * This command implements the indexer state "Receiving."
 * 
 * <p>
 * Note that the initialize method is omitted. The default implementations in
 * the commmand interface is what we need, nothing for initialize.
 * </p>
 */
public class IndexerReceivingState extends CommandBase {

  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerReceivingState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerReceivingState getInstance(final Indexer indexer) {
    if (INSTANCE == null) {
      INSTANCE = new IndexerReceivingState(indexer);
    }
    return INSTANCE;
  }

  /**
   * @param indexer the indexer subsystem to use.
   */
  private IndexerReceivingState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(indexer);
  }

 /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation makes sure we maintain the proper receive cargo speed.
   * </p>
   */
  @Override
  public void execute() {
    indexer.setToReceiveVelocity();
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This command ends normally when the cargo arrives at the sensor.
   * </p>
   */
  @Override
  public boolean isFinished() {
    return this.indexer.isCargoAtSensor();
  }


 /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation schedules the next state in the indexer state machine.
   * RobotCargoCount is incremented to keep track of cargo in robot.
   * </p>
   */
  @Override
  public void end(boolean interrupted) {
    this.indexer.stop();
    IndexerAtSensorState.getInstance(this.indexer).schedule();
  }
}