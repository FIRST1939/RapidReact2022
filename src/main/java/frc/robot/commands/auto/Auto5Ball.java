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
public class Auto5Ball extends SequentialCommandGroup {

  public Auto5Ball(final DriveTrain driveTrain, final Intake intake, final Indexer indexer, final Shooter shooter, final Limelight limelight, final RumbleController rumbleController) {

    this.addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),

        // Gather, move to cargo and set for fender high.
        //This 5ballauto set up at bottom side of the field right next to tarmac angle(...?)
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-45, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.0),
        new WaitCommand(0.15),
        new AutoModeShooter(2, indexer, shooter).withTimeout(1.5),
        new WaitCommand(0.2),

        //after shot 2 balls
        new TurnToAngle(driveTrain, 103.3),
        new ParallelCommandGroup(
          new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
          new DriveStraightDistance(-109.5, driveTrain, 0.75),
          new SetShot(shooter, Constants.SHOTS.cargoRing)
        ),
        new TurnToAngle(driveTrain, -59.5),
        new DriveStraightDistance(23.5, driveTrain, 0.7),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(0.5),
        new WaitCommand(0.5),
        new ManualMoveToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(0.5),
        new WaitCommand(0.5),
        new AutoModeShooter(1, indexer, shooter).withTimeout(1.0)

        //after shot 3rd ball        
        new TurnToAngle(driveTrain, 28.5),
        new ParallelCommandGroup(
          new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
          new DriveStraightDistanceNoStop(-125, driveTrain, 0.8)
        ),

        new ParallelCommandGroup(
            new DriveStraightDistance(-15, driveTrain, 0.5), 
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new WaitCommand(1.5),

        // After intake 5th ball
        new DriveStraightDistance(140, driveTrain, 0.7),
        new TurnToAngle(driveTrain, -28.5),
        new WaitCommand(0.2),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(0.5),
        new WaitCommand(0.3),
        new ManualMoveToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(1.0),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0)

        /*
        //Just in case if we have not enough time
        // disable 71-79 !!swap!!
        new ParallelCommandGroup(
          new DriveStraightDistance(80, driveTrain, 0.7),
          new SetShot(shooter, Constants.SHOTS.launchpad)),
        new TurnToAngle(driveTrain, -20),
        new WaitCommand(0.2),
        new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController).withTimeout(0.5),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0) 
         */
      )
    );
  }
}