package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.commands.ManualMoveAndTurnToTarget;
import frc.robot.commands.ManualTurnToTarget;
import frc.robot.commands.RumbleController;
import frc.robot.commands.ToggleManualEjection;
import frc.robot.commands.intake.IntakeGatheringEmptyState;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

public class Rude2Ball extends SequentialCommandGroup {
    
    public Rude2Ball (final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight, final RumbleController rumbleController) {

        addCommands(
            // Configurable wait for alliance partner.
            new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistanceNoStop(-45, driveTrain, 0.7),
                new SetShot(shooter, Constants.SHOTS.cargoRing)
            ),

            //new DriveStraightDistance(17.1, driveTrain, 0.4),
            new ManualMoveAndTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(2.0),
            new WaitCommand(0.15),
            new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
            new WaitCommand(0.2),

            new TurnToAngle(driveTrain, -112.5),

            new DriveStraightDistanceNoStop(-110, driveTrain, 0.6),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-10, driveTrain, 0.5)
            ),

            new TurnToAngle(driveTrain, -169),

            new DriveStraightDistanceNoStop(-150.65, driveTrain, 0.65),

            new ParallelCommandGroup(
                new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
                new DriveStraightDistance(-10, driveTrain, 0.5),
                new SetShot(shooter, Constants.SHOTS.fenderLow)
            ),

            new TurnToAngle(driveTrain, -67.5),
            
            new ToggleManualEjection(intake, indexer),
            new WaitCommand(1.5),
            new ToggleManualEjection(intake, indexer)
        );
    }
}
