// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake.manual;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.devices.RobotCargoCount;
import frc.robot.subsystems.intake.Intake;

/**
 * A command for intaking in the case of sensor failure or a temporary jam. Be
 * sure to empty all cargo using this command before ending it and thus
 * restarting the default state machine command group.
 * 
 * <p>
 * In a match, we would only enter manual mode if the intake / indexer cargo
 * pipeline was not working due to sensor failure. Therefore, coming out of
 * manual mode in match is not an expected action. However, we need to program
 * for the eventuality and testing. This code assumes that we have emptied the
 * robot of cargo while in manual mode and will re-enter the automated state
 * machine with this assumption.
 */
public class ManualIntakeRollerBelts extends CommandBase {
  private final Intake intake;
  private final DoubleSupplier speedSupplier;

  /**
   * @param intake        the {@link Intake} subsystem being controlled.
   * @param speedSupplier a provider of driver input for intake speed.
   */
  public ManualIntakeRollerBelts(final Intake intake, final DoubleSupplier speedSupplier) {
    this.intake = intake;
    this.speedSupplier = speedSupplier;
    addRequirements(this.intake);
  }

  /**
   * Sets the manual intake speed.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    this.intake.setManualSpeed(this.speedSupplier.getAsDouble());
  }

  /**
   * Stops the intake wheels and belts, and marks the robot as empty of cargo.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void end(boolean interrupted) {
    this.intake.stopIntakeMotor();
    RobotCargoCount.getInstance().decrement();
    RobotCargoCount.getInstance().decrement();
  }
}
