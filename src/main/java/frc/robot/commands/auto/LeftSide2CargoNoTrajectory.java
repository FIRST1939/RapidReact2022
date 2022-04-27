// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;

/**
 * This auto command is to be set up in the left tarmac at and square to the
 * backline (just to the left of the angle furthest from the hub). This should
 * place it directly across from an alliance color cargo with the intake facing
 * the cargo.
 */
public class LeftSide2CargoNoTrajectory extends SequentialCommandGroup {
  /** Creates a new LeftSide2CargoNoTrajectory. */
  public LeftSide2CargoNoTrajectory(
      final DriveTrain driveTrain,
      final Intake intake,
      final Indexer indexer,
      final Shooter shooter) {
    addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),
        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(AutoConstants.CLOSE_CARGO_PICKUP_DRIVE_DIST, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.fenderHigh)),
        // Drive to point straight out from the fender.
        new DriveStraightDistance(-AutoConstants.CLOSE_CARGO_PICKUP_TO_TURN_DIST, driveTrain, 0.5),
        // Turn square to the fender.
        new DriveTurnToRelativeAngle(() -> -AutoConstants.TURN_TO_FENDER_SMALL_ANGLE, driveTrain),
        // Drive to fender with timeout because we may hit and not reach distance.
        new DriveStraightDistance(-AutoConstants.AFTER_TURN_DRIVE_TO_FENDER_DIST, driveTrain, 0.5).withTimeout(3),
        // Shoot with timeout in case of jam.
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        // Do not drive until second shot has cleared shooter.
        new WaitCommand(1.0),
        // Exit tarmac.
        new DriveStraightDistance(AutoConstants.FROM_FENDER_TO_EXIT_TARMAC_DIST, driveTrain, 0.5));
  }
}
