// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.devices.Targeting;

/**
 * Combines instances of {@link TurnToTarget} and {@link MoveToTarget} to point
 * at the target and to move into range for a vision controlled shot.
 */
public class MoveAndTurnToTarget extends SequentialCommandGroup {
  /** Creates a new MoveAndTurnToTarget. */
  public MoveAndTurnToTarget(final DriveTrain driveTrain, final Targeting targeting) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
        new TurnToTarget(driveTrain, targeting).withTimeout(3.0),
        new MoveToTarget(driveTrain, targeting).withTimeout(3.0));
  }
}
