// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.intake.Intake;

/**
 * This is the command to schedule when the button to extend the intake (start
 * gathering) is pressed.
 * <ol>
 * <li>If the state machine is not running, just extend.
 * <li>If the state machine is running request extension (see
 * {@link Intake#requestExtension()}).
 * </ol>
 */
public class IntakeExtendCommandSelector extends CommandBase {
  private final Intake intake;

  /**
   * @param intake the {@link Intake} subsystem being controlled.
   */
  public IntakeExtendCommandSelector(final Intake intake) {
    this.intake = intake;
  }

  /**
   * If the state machine is running, request extension. If not, force extension.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    if (this.intake.isStateMachineRunning()) {
      this.intake.requestExtension();
    } else {
      this.intake.extendIntake();
    }
  }

  /**
   * This is an instant command and ends immediatly.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean isFinished() {
    return true;
  }
}
