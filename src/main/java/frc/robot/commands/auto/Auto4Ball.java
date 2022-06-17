// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.devices.Limelight;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.ManualMoveToTarget;
import frc.robot.subsystems.drive.ManualTurnToTarget;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;

/**
 * This auto command is to be set up in the left tarmac at and square to the
 * backline (just to the left of the angle furthest from the hub). This should
 * place it directly across from an alliance color cargo with the intake facing
 * the cargo.
 */
public class Auto4Ball extends SequentialCommandGroup {
  /** Creates a new Auto4Ball. */
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
            intake.getAutoRequestExtensionCommand(),
            new DriveStraightDistance(-48, driveTrain, 0.5),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new WaitCommand(0.15),
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        new WaitCommand(0.2),
        // new DriveTurnToRelativeAngle(() -> 26, driveTrain).withTimeout(1.0),
        new TurnToAngle(driveTrain, 30),
        new DriveStraightDistanceNoStop(-14, driveTrain, 0.55),
        new ParallelCommandGroup(
            intake.getAutoRequestExtensionCommand(),
            new DriveStraightDistanceNoStop(-113, driveTrain, 0.7)),
        // new WaitCommand(0.2),
        new ParallelCommandGroup(
            new DriveStraightDistance(-17, driveTrain, 0.4),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        new WaitCommand(2.0),
        // new DriveStraightDistance(-140, driveTrain),
        // new WaitCommand(2.0),
        new DriveStraightDistance(10, driveTrain, 0.6),
        new DriveStraightDistance(125, driveTrain, 0.7),
        new DriveStraightDistance(5, driveTrain, 0.5),
        new WaitCommand(0.3),
        // new DriveTurnToRelativeAngle(() -> -30, driveTrain).withTimeout(1.0),
        new TurnToAngle(driveTrain, -30),
        // new TurnToAngle(driveTrain, -30),
        new WaitCommand(0.2),
        new ManualTurnToTarget(driveTrain, limelight, 0).withTimeout(1.0),
        new WaitCommand(0.3),
        new ManualMoveToTarget(driveTrain, limelight, 0).withTimeout(1.5),
        new WaitCommand(0.3),
        new AutoModeShooter(2, indexer, shooter).withTimeout(2.0)

    );

  }
}