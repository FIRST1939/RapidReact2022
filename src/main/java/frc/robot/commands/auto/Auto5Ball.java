package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.devices.Limelight;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.ManualMoveToTarget;
import frc.robot.subsystems.drive.ManualTurnToTarget;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;

/**
 * This auto command is to be set up in the left tarmac at and square to the
 * backline (just to the left of the angle furthest from the hub). This should
 * place it directly across from an alliance color cargo with the intake facing
 * the cargo.
 */
public class Auto5Ball extends SequentialCommandGroup {
  /** Creates a new Auto5Ball. */
  public Auto5Ball(
      final DriveTrain driveTrain,
      final Intake intake,
      final Indexer indexer,
      final Shooter shooter,
      final Limelight limelight) {
    addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),
        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            intake.getAutoRequestExtensionCommand(),
            new DriveStraightDistance(-48, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new WaitCommand(0.15),
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        new WaitCommand(0.2),
        // new DriveTurnToRelativeAngle(() -> 26, driveTrain).withTimeout(1.0),
        new TurnToAngle(driveTrain, 24.5),
        new DriveStraightDistanceNoStop(-14, driveTrain, 0.7),
        new ParallelCommandGroup(
            intake.getAutoRequestExtensionCommand(),
            new DriveStraightDistanceNoStop(-113, driveTrain, 0.8)),
        new ParallelCommandGroup(
            new DriveStraightDistance(-17, driveTrain, 0.6),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new WaitCommand(2.0),

        // after intake 2ball in humanplayer station
        new TurnToAngle(driveTrain, 7.937),
        new DriveStraightDistance(240, driveTrain, 0.7),
        new DriveStraightDistance(16, driveTrain, 0.5),
        new WaitCommand(0.3),
        new TurnToAngle(driveTrain, -106.42),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new ManualMoveToTarget(driveTrain, limelight, 0).withTimeout(1.5),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0),

        // after shoot 4ball
        new ParallelCommandGroup(
            new DriveStraightDistance(-45, driveTrain, 0.7),
            intake.getAutoRequestExtensionCommand(),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0),
        new ManualMoveToTarget(driveTrain, limelight, 0),
        new AutoModeShooter(1, indexer, shooter).withTimeout(3.0));

  }
}