// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.RobotCargoCount;
import frc.robot.subsystems.intake.Intake;

public class IntakeStowedHoldState extends CommandBase {
  private static IntakeStowedHoldState INSTANCE;

  private final Intake intake;

  public static final synchronized IntakeStowedHoldState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeStowedHoldState(intake);
    }
    return INSTANCE;
  }

  /** Creates a new IndexStowedHoldState. */
  private IntakeStowedHoldState(final Intake intake) {
    this.intake = intake;
    addRequirements(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.intake.retractIntake();
    this.intake.stopIntakeMotor();
    Lights.getInstance().setColor(LEDMode.STROBE);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !RobotCargoCount.getInstance().isFull();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (!this.intake.isManualMode()) {
      PostLoopCommandScheduler.addCommandToSchedule(
          IntakeStowedSendState.getInstance(this.intake));
    }
  }
}
