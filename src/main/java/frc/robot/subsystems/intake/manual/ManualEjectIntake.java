// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import frc.robot.subsystems.intake.Intake;

/**
 * A specialization of {@link ManualIntakeRollerBelts} for cargo ejection.
 * It runs outward at a fixed speed.
 */
public class ManualEjectIntake extends ManualIntakeRollerBelts {
  /**
   * Creates an instance that runs outward until cancelled.
   * 
   * @param intake the intake being controlled.
   */
  public ManualEjectIntake(final Intake intake) {
    super(intake, () -> 0.8);
  }
}
