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
 * 
 * <ol>
 * <li>If in manual mode, just retract.
 * <li>If automated and retracted, ignore.
 * <li>If automated and not retracted, cancel active intake state and let it
 * pick next state.
 * </ol>
 */
public class IntakeRetractCommandSelector extends CommandBase {

  private final Intake intake;
  private final ManualIntakeRetract manualRetract;

  public IntakeRetractCommandSelector (final Intake intake) {

    this.intake = intake;
    this.manualRetract = new ManualIntakeRetract(this.intake);
  }

  @Override
  public void initialize () {

    if (this.intake.isManualMode()) {

      this.manualRetract.schedule();
    } else {

      if (!this.intake.isRetracted()) {

        final Command currentState = this.intake.getCurrentCommand();

        if (currentState != null) {

          currentState.cancel();
        }
      }
    }
  }

  @Override
  public boolean isFinished () {
    
    return true;
  }
}
