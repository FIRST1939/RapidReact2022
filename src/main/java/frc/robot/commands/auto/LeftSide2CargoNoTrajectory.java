// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
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
public class LeftSide2CargoNoTrajectory extends SequentialCommandGroup {
  /** Creates a new LeftSide2CargoNoTrajectory. */
  public LeftSide2CargoNoTrajectory(
      final DriveTrain driveTrain,
      final Intake intake,
      final Indexer indexer,
      final Shooter shooter) {
    // TODO add dashboard settable WaitCommand to start.
    addCommands(
        new ParallelCommandGroup(
            IntakeGatheringEmptyState.getInstance(intake),
            new DriveStraightDistance(48.00, driveTrain),
            new SetShot(shooter, Constants.SHOTS.fenderHigh)),
        new DriveStraightDistance(-100.0, driveTrain),
        new DriveTurnToRelativeAngle(-22.5, driveTrain),
        new DriveStraightDistance(-50.0, driveTrain),
        new AutoModeShooter(2, indexer, shooter),
        new DriveStraightDistance(150.0, driveTrain));
  }
}
