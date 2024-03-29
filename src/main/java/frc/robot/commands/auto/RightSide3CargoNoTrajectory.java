package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.intake.IntakeGatheringEmptyState;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

/**
 * This auto command is to be set up in the right tarmac at and square to the
 * backline (just to the right of the angle furthest from the hub). This should
 * place it directly across from an alliance color cargo with the intake facing
 * the cargo.
 */
public class RightSide3CargoNoTrajectory extends SequentialCommandGroup {

  public RightSide3CargoNoTrajectory(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter) {

    this.addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(AutoConstants.CLOSE_CARGO_PICKUP_DRIVE_DIST, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),

        // Drive to point straight out from the fender.
        //new DriveStraightDistance(-AutoConstants.CLOSE_CARGO_PICKUP_TO_TURN_DIST, driveTrain),
        // Turn square to the fender.
        //new DriveTurnToRelativeAngle(() -> AutoConstants.TURN_TO_FENDER_SMALL_ANGLE, driveTrain),
        // Drive to fender with timeout because we may hit and not reach distance.
        //new DriveStraightDistance(-AutoConstants.AFTER_TURN_DRIVE_TO_FENDER_DIST, driveTrain).withTimeout(3),

        // Shoot with timeout in case of jam.
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),

        // Do not drive until second shot has cleared shooter.
        new WaitCommand(1.0),
        // Move away from fender and set for cargo ring.
        new ParallelCommandGroup(
            new DriveStraightDistance(AutoConstants.AFTER_TURN_DRIVE_TO_FENDER_DIST, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),

        // Turn toward cargo.
        new DriveTurnToRelativeAngle(
            () -> AutoConstants.TURN_TO_THIRD_CARGO_ANGLE,
            driveTrain),
        // Gather and move to cargo.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(AutoConstants.CLOSE_CARGO_PICKUP_TO_TURN_DIST, driveTrain, 0.5)),
            new DriveTurnToRelativeAngle(() -> -12, driveTrain),

        // Shoot
        new AutoModeShooter(1, indexer, shooter).withTimeout(3.0),
        // Do not set shot until shot has cleared shooter.
        new WaitCommand(1.0),
        new SetShot(shooter, Constants.SHOTS.fenderHigh)
    );
  }
}
