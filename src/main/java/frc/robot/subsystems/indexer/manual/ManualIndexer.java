// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer.manual;

import static frc.robot.Constants.IndexerConstants.MANUAL_INDEXER_FEED_OUTPUT;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.devices.RobotCargoCount;
import frc.robot.subsystems.indexer.Indexer;

/**
 * A command for indexing in the case of sensor failure or a temporary jam. Be
 * sure to empty all cargo using this command before ending it and thus
 * restarting the default state machine command group.
 * 
 * <p>
 * In a match, we would only enter manual mode if the intake / indexer cargo
 * pipeline was not working due to sensor failure. Therefore, coming out of
 * manual mode in match is not an expected action. However, we need to program
 * for the eventuality and testing. This code assumes that we have emptied the
 * robot of cargo while in manual mode and will re-enter the automated state
 * machine with this assumption.
 */
public class ManualIndexer extends CommandBase {
  private final Indexer indexer;
  private final DoubleSupplier speedSupplier;
  private final Trigger shootTrigger;

  /**
   * If the manual shoot trigger returns true, the speed supplier is ignored. A
   * fixed shooter feed speed is used instead. This enables two scenarios. First,
   * a fixed shooter feed speed for more consistent shooting. Second, the use of a
   * supplier with known characteristics for a specific task (e.g. ejection) by
   * using a trigger that is always inactive.
   * 
   * @param indexer       the {@link Indexer} subsystem being controlled.
   * @param speedSupplier a provider of driver input for indexer speed.
   * @param shootTrigger  a manual shoot trigger.
   */
  public ManualIndexer(
      final Indexer indexer,
      final DoubleSupplier speedSupplier,
      final Trigger shootTrigger) {
    this.indexer = indexer;
    this.speedSupplier = speedSupplier;
    this.shootTrigger = shootTrigger;
    addRequirements(this.indexer);
  }

  /**
   * Sets the speed to shooter feeding (trigger true) or the supplier speed
   * (trigger false).
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    this.indexer.setManualSpeed(
        this.shootTrigger.get()
            ? MANUAL_INDEXER_FEED_OUTPUT
            : this.speedSupplier.getAsDouble());
  }

  /**
   * Stops the indexer and marks the robot as empty of cargo.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
    this.indexer.stop();
    RobotCargoCount.getInstance().decrement();
    RobotCargoCount.getInstance().decrement();
  }
}
