// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.Intake;

public class IntakeStowedEmptyState extends CommandBase {

  private static IntakeStowedEmptyState INSTANCE;

  /** This command's required indexer subsystem. */
  private final Intake intake;

  public static final synchronized IntakeStowedEmptyState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeStowedEmptyState(intake);
    }
    return INSTANCE;
  }

  /** Creates a new IntakeStowedEmptyState. */
  public IntakeStowedEmptyState(final Intake intake) {
    this.intake = intake;
    addRequirements(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.intake.stowIntake();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !this.intake.getIntakeDeployment();
  }


 // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //TODO schedule next state.
  }
}