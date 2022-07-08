// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.intake.Intake;

/**
 * This is the command to schedule when the button to retract the intake (stop
 * gathering) is pressed.
 * <ol>
 * <li>If the state machine is not running, just retract.
 * <li>If the state machine is running request retraction (see
 * {@link Intake#requestRetraction()}).
 * </ol>
 */
public class IntakeRetractCommandSelector extends CommandBase {
  private final Intake intake;

  /**
   * @param intake the {@link Intake} subsystem being controlled.
   */
  public IntakeRetractCommandSelector(final Intake intake) {
    this.intake = intake;
  }

  /**
   * If the state machine is running, request retraction. If not, force
   * retraction.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    if (this.intake.isStateMachineRunning()) {
      this.intake.requestRetraction();
    } else {
      this.intake.retractIntake();
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
