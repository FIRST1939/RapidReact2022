package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;

public class ManualEjectIndexer extends ManualIndexer {

  /**
   * Creates an instance that runs outward until cancelled.
   * 
   * @param indexer the indexer being controlled.
   */
  public ManualEjectIndexer (final Indexer indexer) {
    
    super(indexer, () -> 0.6, new Trigger());
  }
}
