// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.RobotCargoCount;


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
    this.intake.extendIntake();
  }

  // Called when the command executes.
  @Override
  public void execute(){
    this.intake.setIntakeSpeed(Constants.INTAKE_GATHERING_EMPTY_VELOCITY);
    //TODO Make intake speed based on drivetrain
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
      RobotCargoCount.getInstance().increment();
      //TODO schedule AtSensor state
    }
    else{
    //TODO Schedule IntakeStowedEmptyState
    }
  }
}