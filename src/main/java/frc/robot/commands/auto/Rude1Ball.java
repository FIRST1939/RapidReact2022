package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.multisub.ToggleManualEjection;
import frc.robot.devices.Limelight;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.MoveAndTurnToTarget;
import frc.robot.subsystems.drive.ResetGyro;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;

public class Rude1Ball extends SequentialCommandGroup {

    public Rude1Ball(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter,
            final Limelight limelight) {
        addCommands(
                // Configurable wait for alliance partner.
                new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

                new ParallelCommandGroup(
                        intake.getAutoRequestExtensionCommand(),
                        new DriveStraightDistance(-45, driveTrain, 0.5),
                        new SetShot(shooter, Constants.SHOTS.cargoRing)),

                new MoveAndTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
                new WaitCommand(0.15),
                new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
                new WaitCommand(0.2),

                // after shoot 2ball
                new TurnToAngle(driveTrain, 84),
                intake.getAutoRequestExtensionCommand(),
                new DriveStraightDistanceNoStop(-55, driveTrain, 0.5),
                new WaitCommand(0.3),
                new ResetGyro(driveTrain),
                new WaitCommand(0.5),

                // new TurnToAngle(driveTrain, -115),
                new TurnToAngle(driveTrain, -35.0),
                new WaitCommand(0.3),

                /*
                 * new ParallelCommandGroup(
                 * new SetShot(shooter, Constants.SHOTS.fenderPlusOneLow)
                 * //new DriveStraightDistance(20, driveTrain, .55)
                 * ),
                 */

                new ToggleManualEjection(intake, indexer),
                new WaitCommand(1.5),
                new ToggleManualEjection(intake, indexer)
        // new AutoModeShooter(1, indexer, shooter)
        );
    }
}