// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.devices.Targeting;

/**
 * Combines instances of {@link TurnToTarget} and {@link MoveToCargoRing} to
 * point at the target and to move into range for a cargo ring shot.
 */
public class TurnToTargetThenMoveToCargoRing extends SequentialCommandGroup {
  /**
   * Creates a new TurnToTargetThenMoveToCargoRing.
   * 
   * @param driveTrain the {@link DriveTrain} to turn and move.
   * @param targeting  the {@link Targeting} implementation used to find the
   *                   target as input to turn angle and distance calculation.
   */
  public TurnToTargetThenMoveToCargoRing(final DriveTrain driveTrain, final Targeting targeting) {
    addCommands(
        new TurnToTarget(driveTrain, targeting).withTimeout(3.0),
        new MoveToCargoRing(driveTrain, targeting).withTimeout(3.0));
  }
}
