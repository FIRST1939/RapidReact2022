// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
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
  private final Command manualRetract;

  /** Creates a new IntakeExtendCommandSelector. */
  public IntakeRetractCommandSelector(final Intake intake) {
    this.intake = intake;
    this.manualRetract = new InstantCommand(intake::retractIntake, intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (!this.intake.isStateMachineRunning()) {
      this.manualRetract.schedule();
    } else {
      this.intake.requestRetraction();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
