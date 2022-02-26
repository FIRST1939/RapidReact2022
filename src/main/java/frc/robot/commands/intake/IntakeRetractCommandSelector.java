// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

/**
 * This is the command to schedule when the button to retract the intake (stop
 * gathering) is pressed. It will handle three cases.
 * <ol>
 * <li>If in manual mode, just retract.
 * <li>If automated and command running, cancel it.
 * <li>If automated and no command running, schedule based on sensor coverage.
 * </ol>
 */
public class IntakeRetractCommandSelector extends CommandBase {
  private final Intake intake;
  private final ManualIntakeRetract manualRetract;

  /** Creates a new IntakeExtendCommandSelector. */
  public IntakeRetractCommandSelector(final Intake intake) {
    this.intake = intake;
    this.manualRetract = new ManualIntakeRetract(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (this.intake.isManualMode()) {
      this.manualRetract.schedule();
    } else {
      final Command currentState = this.intake.getCurrentCommand();
      if (currentState != null) {
        currentState.cancel();
      } else {
        if (!this.intake.isCargoAtSensor()) {
          IntakeGatheringEmptyState.getInstance(this.intake).schedule();
        } else {
          // TODO schedule stowed hold command state
        }
      }
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
