// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.intake.Intake;

/**
 * This is the command to schedule when the button to extend the intake (start
 * gathering) is pressed. It will handle three cases.
 * <ol>
 * <li>If in manual mode, just extend.
 * <li>If automated, intake empty, and robot not full, start gathering.
 * <li>If automated and in any other state, ignore.
 * </ol>
 */
public class IntakeExtendCommandSelector extends CommandBase {
  private final Intake intake;
  private final ManualIntakeExtend manualExtend;

  /** Creates a new IntakeExtendCommandSelector. */
  public IntakeExtendCommandSelector(final Intake intake) {
    this.intake = intake;
    this.manualExtend = new ManualIntakeExtend(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (!this.intake.isStateMachineRunning()) {
      this.manualExtend.schedule();
    } else {
      this.intake.requestExtension();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
