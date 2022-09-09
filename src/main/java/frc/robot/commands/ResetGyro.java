package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.DriveTrain;

public class ResetGyro extends InstantCommand {

  private final DriveTrain driveTrain;

  public ResetGyro(final DriveTrain driveTrain) {

    this.driveTrain = driveTrain;
  }

  @Override
  public void initialize () {

    this.driveTrain.resetYaw();
  }
}
