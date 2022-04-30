// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
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
  private final Command manualExtend;

  /** Creates a new IntakeExtendCommandSelector. */
  public IntakeExtendCommandSelector(final Intake intake) {
    this.intake = intake;
    this.manualExtend = new InstantCommand(intake::extendIntake, intake);
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
