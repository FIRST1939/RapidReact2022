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
import frc.robot.commands.ResetGyro;
import frc.robot.commands.RumbleController;
import frc.robot.commands.ToggleManualEjection;
import frc.robot.commands.intake.IntakeGatheringEmptyState;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.indexer.Indexer;

public class Rude1Ball extends SequentialCommandGroup {

    public Rude1Ball (final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight, final RumbleController rumbleController) {


    addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-45, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)
        ),

        new ManualMoveAndTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.0),
        new WaitCommand(0.15),
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        new WaitCommand(0.2),

        //after shoot 2ball 
        new TurnToAngle(driveTrain, 84),
        new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
        new DriveStraightDistanceNoStop(-55, driveTrain, 0.5),
        new WaitCommand(0.3),
        new ResetGyro(driveTrain),
        new WaitCommand(0.5),

        //new TurnToAngle(driveTrain, -115),
        new TurnToAngle(driveTrain, -35.0),
        new WaitCommand(0.3),

        /*
        new ParallelCommandGroup(
            new SetShot(shooter, Constants.SHOTS.fenderPlusOneLow)
            //new DriveStraightDistance(20, driveTrain, .55)
        ),
        */

        new ToggleManualEjection(intake, indexer),
        new WaitCommand(1.5),
        new ToggleManualEjection(intake, indexer)
        //new AutoModeShooter(1, indexer, shooter)
        );
    }
}