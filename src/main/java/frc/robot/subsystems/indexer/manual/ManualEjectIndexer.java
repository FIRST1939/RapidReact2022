// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer.manual;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.indexer.Indexer;

/**
 * A specialization of {@link ManualIndexer} for cargo ejection. It runs outward
 * at a fixed speed.
 */
public class ManualEjectIndexer extends ManualIndexer {
  /**
   * Creates an instance that runs outward until cancelled.
   * 
   * @param indexer the indexer being controlled.
   */
  public ManualEjectIndexer(final Indexer indexer) {
    super(indexer, () -> 0.6, new Trigger());
  }
}
