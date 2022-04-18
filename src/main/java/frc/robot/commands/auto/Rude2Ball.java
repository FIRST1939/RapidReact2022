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
                new DriveStraightDistance(-61, driveTrain, 0.5),
                new SetShot(shooter, Constants.SHOTS.cargoRing)
            ),

            new DriveStraightDistance(17.1, driveTrain, 0.4),
            new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
            new WaitCommand(0.15),
            new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
            new WaitCommand(0.2),

            new TurnToAngle(driveTrain, -68.2),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-60.6, driveTrain, 0.5)
            ),

            new TurnToAngle(driveTrain, 168.6),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-160.65, driveTrain, 0.5),
                new SetShot(shooter, Constants.SHOTS.fenderLow)
            ),

            new TurnToAngle(driveTrain, -126.5),
            new DriveStraightDistance(-89.7, driveTrain, 0.4),

            new TurnToAngle(driveTrain, 15.9),
            new AutoModeShooter(2, indexer, shooter).withTimeout(3.0)
        );
    }
}
