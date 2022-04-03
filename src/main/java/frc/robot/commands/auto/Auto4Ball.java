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
import frc.robot.Limelight;
import frc.robot.commands.ManualMoveToTarget;
import frc.robot.commands.ManualTurnToTarget;
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
public class Auto4Ball extends SequentialCommandGroup {
  /** Creates a new LeftSide2CargoNoTrajectory. */
  public Auto4Ball(
      final DriveTrain driveTrain,
      final Intake intake,
      final Indexer indexer,
      final Shooter shooter,
      final Limelight limelight) {
    addCommands(

        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),
        // Gather, move to cargo and set for fender high.
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-48, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        //new WaitCommand(0.5),
        new DriveTurnToRelativeAngle(() -> 26, driveTrain).withTimeout(1.0),
        new DriveStraightDistance(-14, driveTrain, 0.55),
        new DriveStraightDistance(-110, driveTrain, 0.7), 
        new WaitCommand(0.2),
        new ParallelCommandGroup(
            new ScheduleCommand(IntakeGatheringEmptyState.getInstance(intake)),
            new DriveStraightDistance(-14, driveTrain, 0.55), 
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
            new WaitCommand(2.5),
        //new DriveStraightDistance(-140, driveTrain),
        //new WaitCommand(2.0),
        new DriveStraightDistance(10, driveTrain, 0.6),
        new DriveStraightDistance(130, driveTrain, 0.7),
        //new DriveStraightDistance(10, driveTrain, 0.6),
        new WaitCommand(0.3),
        new DriveTurnToRelativeAngle(() -> -30, driveTrain).withTimeout(1.0),
        new WaitCommand(0.2),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new WaitCommand(0.3),
        new ManualMoveToTarget(driveTrain, limelight, 0, null).withTimeout(1.5),
        new DriveStraightDistance(6, driveTrain, 0.5),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0)

    );

  }
}