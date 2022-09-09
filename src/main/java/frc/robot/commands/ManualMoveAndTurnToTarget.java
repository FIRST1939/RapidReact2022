package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Limelight;
import frc.robot.subsystems.DriveTrain;

public class ManualMoveAndTurnToTarget extends SequentialCommandGroup {

  public ManualMoveAndTurnToTarget(final DriveTrain driveTrain, final Limelight limelight, final int pipeline, final RumbleController rumbleController) {

    this.addCommands(
      new ManualTurnToTarget(driveTrain, limelight, pipeline, rumbleController).withTimeout(3.0),
      new ManualMoveToTarget(driveTrain, limelight, pipeline, rumbleController).withTimeout(3.0)
    );
  }
}
