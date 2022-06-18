package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.multisub.ToggleManualEjection;
import frc.robot.devices.Limelight;
import frc.robot.subsystems.drive.DriveStraightDistance;
import frc.robot.subsystems.drive.DriveStraightDistanceNoStop;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.MoveAndTurnToTarget;
import frc.robot.subsystems.drive.ResetGyro;
import frc.robot.subsystems.drive.TurnToAngle;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;

public class Rude2Ball extends SequentialCommandGroup {

    public Rude2Ball(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter,
            final Limelight limelight) {

        addCommands(
                // Configurable wait for alliance partner.
                new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

                new ParallelCommandGroup(
                        intake.getAutoRequestExtensionCommand(),
                        new DriveStraightDistanceNoStop(-40, driveTrain, 0.6),
                        new SetShot(shooter, Constants.SHOTS.cargoRing)),

                // new DriveStraightDistance(17.1, driveTrain, 0.4),
                new MoveAndTurnToTarget(driveTrain, limelight, 0).withTimeout(2.0),
                new WaitCommand(0.15),
                new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
                new WaitCommand(0.2),

                new TurnToAngle(driveTrain, -90),

                new DriveStraightDistanceNoStop(-50, driveTrain, 0.6),

                new ParallelCommandGroup(
                        intake.getAutoRequestExtensionCommand(),
                        new DriveStraightDistance(-50, driveTrain, 0.5)),

                new TurnToAngle(driveTrain, 180),

                new ResetGyro(driveTrain),

                new WaitCommand(0.2),

                // ERROR HAPPENS HERE AFTER TURNTOANGLE
                new DriveStraightDistance(-140.65, driveTrain, 0.65),

                new ParallelCommandGroup(
                        intake.getAutoRequestExtensionCommand(),
                        new DriveStraightDistance(-10, driveTrain, 0.5),
                        new SetShot(shooter, Constants.SHOTS.fenderLow)),

                new TurnToAngle(driveTrain, -35.0),

                new ToggleManualEjection(intake, indexer),
                new WaitCommand(2.0),
                new ToggleManualEjection(intake, indexer));
    }
}
