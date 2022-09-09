package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Limelight;
import frc.robot.Constants.SHOTS;
import frc.robot.commands.ManualMoveAndTurnToTarget;
import frc.robot.commands.RumbleController;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

public class OneBall extends SequentialCommandGroup {

  public OneBall (final Shooter shooter, final Indexer indexer, final DriveTrain driveTrain, final Limelight limelight, final RumbleController rumbleController) {

    this.addCommands(
      new SetShot(shooter, SHOTS.cargoRing),
      new DriveStraightDistance(-47, driveTrain, 0.5),
      new WaitCommand(0.5),

      //new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController),
      //new WaitCommand(0.5),

      new ManualMoveAndTurnToTarget(driveTrain, limelight, 0, rumbleController),
      new WaitCommand(1.0),
      new AutoModeShooter(1, indexer, shooter).withTimeout(3.0),

      // Do not drive until second shot has cleared shooter.
      new WaitCommand(0.5),
      // Exit tarmac.
      new DriveStraightDistance(-5, driveTrain, 0.5).withTimeout(4.0)
    );
  }
}
