// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.Intake;


public class IntakeGatheringEmptyState extends CommandBase {
 
  private static IntakeGatheringEmptyState INSTANCE;

  /** This command's required intake subsystem. */
  private final Intake intake;

  public static final synchronized IntakeGatheringEmptyState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeGatheringEmptyState(intake);
    }
    return INSTANCE;
  }
 /** Creates a new IntakeGatheringEmptyState. */
  public IntakeGatheringEmptyState(final Intake intake) {
    this.intake = intake;
    addRequirements(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //this.intake.deployIntake();
    //TODO double check deployIntake method in Intake
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return this.intake.isCargoAtSensor();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (!interrupted){
      //TODO increment cargo count
    }
    //TODO schedule AtSensor state
  }
}
