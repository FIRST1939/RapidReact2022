// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.RobotCargoCount;

public class IntakeAtSensorState extends CommandBase {

  private static IntakeAtSensorState INSTANCE;

  /** This command's required intake subsystem. */
  private final Intake intake;

  public static final synchronized IntakeAtSensorState getInstance(final Intake intake) {
    if (INSTANCE == null) {
      INSTANCE = new IntakeAtSensorState(intake);
    }
    return INSTANCE;
  }

  /** Creates a new IntakeAtSensorState. */
  public IntakeAtSensorState(final Intake intake) {
    this.intake = intake;
    addRequirements(intake);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }

// Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (interrupted){
      //TODO schedule StowedSend state
    }
    else if (RobotCargoCount.getInstance().get() == 1){
      //TODO schedule GatheringSend state
    }
    else if (RobotCargoCount.getInstance().get() == 2){
      //TODO schedule StowedHold state
    }
  }
}
