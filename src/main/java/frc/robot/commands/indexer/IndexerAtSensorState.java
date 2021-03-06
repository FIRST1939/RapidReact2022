// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Indexer;

public class IndexerAtSensorState extends CommandBase {

  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerAtSensorState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  private long startTime;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerAtSensorState getInstance(final Indexer indexer) {
    if (INSTANCE == null) {
      INSTANCE = new IndexerAtSensorState(indexer);
    }
    return INSTANCE;
  }

  /** Creates a new IndexerAtSensorState. */
  private IndexerAtSensorState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(this.indexer);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.indexer.setToReceiveVelocity();

    startTime = System.currentTimeMillis();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return System.currentTimeMillis() - startTime >= Constants.INDEXER_AT_SENSOR_TIME_ADJUSTMENT_MS;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (!this.indexer.isManualMode()) {
      PostLoopCommandScheduler.addCommandToSchedule(
          IndexerReadyToShootState.getInstance(this.indexer));
    }
  }
}
