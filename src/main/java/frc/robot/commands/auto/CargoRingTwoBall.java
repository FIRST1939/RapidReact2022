package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.commands.ManualMoveAndTurnToTarget;
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
public class CargoRingTwoBall extends SequentialCommandGroup {

  public CargoRingTwoBall(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight, final RumbleController rumbleController) {

    this.addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-40, driveTrain, 0.4),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),

        // Drive to point straight out from the fender.
        new WaitCommand(1.0),
        new ManualMoveAndTurnToTarget(driveTrain, limelight, 0, rumbleController),
        new WaitCommand(2.0),

        //new DriveStraightDistance(40.0, driveTrain),
        // Turn square to the fender.
        //new WaitCommand(1.0),

        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        // Do not drive until second shot has cleared shooter.
        new WaitCommand(1.0),
        // Exit tarmac.
        new DriveStraightDistance(-10, driveTrain, 0.4)
    );
  }
}
