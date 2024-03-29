package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.commands.ManualMoveToTarget;
import frc.robot.commands.ManualTurnToTarget;
import frc.robot.commands.RumbleController;
import frc.robot.commands.intake.IntakeGatheringEmptyState;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

/**
 * This auto command is to be set up in the left tarmac at and square to the
 * backline (just to the left of the angle furthest from the hub). This should
 * place it directly across from an alliance color cargo with the intake facing
 * the cargo.
 */
public class Auto4Ball extends SequentialCommandGroup {

  public Auto4Ball(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight, final RumbleController rumbleController) {

    this.addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-48, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.0),
        new WaitCommand(0.15),
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        new WaitCommand(0.2),

        new TurnToAngle(driveTrain, 28.5),
        new DriveStraightDistanceNoStop(-14, driveTrain, 0.55),
        new ParallelCommandGroup(
          new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
          new DriveStraightDistanceNoStop(-113, driveTrain, 0.7)
        ),

        new ParallelCommandGroup(
            new DriveStraightDistance(-17, driveTrain, 0.4), 
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
            new WaitCommand(2.0),

        new DriveStraightDistance(10, driveTrain, 0.6),
        new DriveStraightDistance(125, driveTrain, 0.7),
        new DriveStraightDistance(5, driveTrain, 0.5),
        new WaitCommand(0.3),

        new TurnToAngle(driveTrain, -28.5),

        new WaitCommand(0.2),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.0),
        new WaitCommand(0.3),
        new ManualMoveToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.5),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0)
    );
  }
}