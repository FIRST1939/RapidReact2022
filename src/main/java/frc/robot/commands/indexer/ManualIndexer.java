// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.indexer;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.RobotCargoCount;

public class ManualIndexer extends CommandBase {
  private final Indexer indexer;
  private final DoubleSupplier speedSupplier;

  /** Creates a new ManualIndexer. */
  public ManualIndexer(final Indexer indexer, final DoubleSupplier speedSupplier) {
    this.indexer = indexer;
    this.speedSupplier = speedSupplier;
    addRequirements(this.indexer);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.indexer.setManualSpeed(this.speedSupplier.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.indexer.stop();
    // In a match, we would only enter manual mode if the intake / indexer cargo
    // pipeline was not working due to sensor failure. Therefore, coming out of
    // manual mode in match is not an expected action. However, we need to program
    // for the eventuality and testing. This code assumes that we have emptied the
    // robot of cargo while in manual mode and will re-enter the automated state
    // machine with this assumption.
    RobotCargoCount.getInstance().decrement();
    RobotCargoCount.getInstance().decrement();
    IndexerEmptyState.getInstance(this.indexer).schedule();
  }
}