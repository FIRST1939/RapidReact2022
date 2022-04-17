package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.commands.ManualTurnToTarget;
import frc.robot.commands.intake.IntakeGatheringEmptyState;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

public class Rude2Ball extends SequentialCommandGroup {
    
    public Rude2Ball (final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight) {

        addCommands(
            // Configurable wait for alliance partner.
            new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-45, driveTrain, 0.5),
                new SetShot(shooter, Constants.SHOTS.cargoRing)
            ),

            new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
            new WaitCommand(0.15),
            new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
            new WaitCommand(0.2),

            new TurnToAngle(driveTrain, -112.5),
            //new TurnToAngle(driveTrain, 112.5), 
            new DriveStraightDistanceNoStop(-94.5, driveTrain, 0.7),
            new WaitCommand(0.3),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-25.5, driveTrain, 0.55)
            ),

            new TurnToAngle(driveTrain, -168.745),
            //new TurnToAngle(driveTrain, 168.745),

            new DriveStraightDistanceNoStop(-135, driveTrain, .8),
            new WaitCommand(0.3),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-40, driveTrain, 0.55)
            ),

            new TurnToAngle(driveTrain, 110),
            //new DriveStraightDistanceNoStop(95, driveTrain, .7),
            new WaitCommand(0.3),

            new ParallelCommandGroup(
                new SetShot(shooter, Constants.SHOTS.fenderLow),
                new DriveStraightDistance(20, driveTrain, .55)
            ),

            //new TurnToAngle(driveTrain, -25),
            new AutoModeShooter(2, indexer, shooter)
        );
    }
}