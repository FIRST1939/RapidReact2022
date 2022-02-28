// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Intake;

public class IntakeStowedSendState extends CommandBase {

  private static IntakeStowedSendState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Intake intake;

  public static final synchronized IntakeStowedSendState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeStowedSendState(intake);
    }
    return INSTANCE;
  }

  /** Creates a new IntakeStowedSend. */
  private IntakeStowedSendState(final Intake intake) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.intake = intake;
    addRequirements(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.intake.retractIntake();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.intake.setIntakeSpeed(Constants.INTAKE_SENDING_VELOCITY);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.intake.stopIntakeMotor();
    if (!this.intake.isManualMode()) {
      IntakeStowedEmptyState.getInstance(this.intake).schedule();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !this.intake.isCargoAtSensor();
  }
}
