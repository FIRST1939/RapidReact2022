// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;

public class IndexerEmptyState extends CommandBase {
  /**
   * Commands in a WPILib robot implementation are often singletons but not
   * always. In the case here where we are implementing a state machine, it is a
   * best practice and sometimes required. For clarity and ease of state
   * transition implementations, we will enforce it for our state commands.
   */
  private static IndexerEmptyState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Indexer indexer;

  /**
   * Must be used to access the singleton instance of this command.
   * 
   * @param indexer the indexer subsystem to use.
   * @return the command instance for the indexer state machine.
   */
  public static final synchronized IndexerEmptyState getInstance(final Indexer indexer) {
    if (INSTANCE == null) {
      INSTANCE = new IndexerEmptyState(indexer);
    }
    return INSTANCE;
  }

  /** Creates a new IndexerEmptyState. */
  private IndexerEmptyState(final Indexer indexer) {
    this.indexer = indexer;
    addRequirements(this.indexer);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.indexer.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return this.indexer.isPriorStageSending();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (!this.indexer.isManualMode()) {
      IndexerReceivingState.getInstance(this.indexer).schedule();
    }
  }
}
