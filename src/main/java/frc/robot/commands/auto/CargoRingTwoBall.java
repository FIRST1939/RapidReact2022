// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.state.SequentialCommandGroup2;
import frc.robot.devices.Limelight;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.MoveAndTurnToTarget;
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
public class CargoRingTwoBall extends SequentialCommandGroup2 {
  /** Creates a new CargoRingTwoBall. */
  public CargoRingTwoBall(
      final DriveTrain driveTrain,
      final Intake intake,
      final Indexer indexer,
      final Shooter shooter,
      final Limelight limelight) {
    addCommands(
        // Configurable wait for alliance partner.
        new WaitCommand(SmartDashboard.getNumber("Auto Start Wait", 0.0)),
        // Gather, move to cargo and set shot for cargo ring.
        new ParallelCommandGroup(
            intake.getAutoRequestExtensionCommand(),
            new DriveStraightDistance(-40, driveTrain, 0.4),
            new SetShot(shooter, Constants.SHOTS.cargoRing)),
        // Drive and turn to square up shot.
        new WaitCommand(1.0),
        new MoveAndTurnToTarget(driveTrain, limelight, 0),
        new WaitCommand(2.0),
        // Shoot 2 cargo.
        new AutoModeShooter(2, indexer, shooter).withTimeout(3.0),
        // Do not drive until second shot has cleared shooter.
        new WaitCommand(1.0),
        // Exit tarmac.
        new DriveStraightDistance(-10, driveTrain, 0.4));
  }
}