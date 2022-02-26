// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class IntakeGatheringSendState extends CommandBase {

  private static IntakeGatheringSendState INSTANCE;

  /** This command's required intake subsystem. */
  private final Intake intake;

  public static final synchronized IntakeGatheringSendState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeGatheringSendState(intake);
    }
    return INSTANCE;
  }
  
  /** Creates a new IntakeGatheringSendState. */
  public IntakeGatheringSendState(final Intake intake) {
    this.intake = intake;
    addRequirements(this.intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //TODO Maybe set speed to be able to move cargo to tower
  }

  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      //TODO Schedule stowed send
    }
    else {
      IntakeGatheringEmptyState.getInstance(this.intake).schedule();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !this.intake.isCargoAtSensor();
  }
}
