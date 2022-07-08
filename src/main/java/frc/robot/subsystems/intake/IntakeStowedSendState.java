// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Sends a cargo from the intake to the next stage. A timeout is used to make
 * sure the intake runs long enough to complete the handoff after the cargo
 * leaves the intake cargo sensor.
 */
class IntakeStowedSendState extends CommandBase {
  /** This command's required indexer subsystem. */
  private final Intake intake;

  /** Used to run for a period after cargo leaves sensor. */
  private long timeout = 0;

  /** Creates a new IntakeStowedSend. */
  IntakeStowedSendState(final Intake intake) {
    this.intake = intake;
    addRequirements(this.intake);
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Makes sure the intake is retracted and initializes the timer.
   */
  @Override
  public void initialize() {
    this.intake.retractIntake();
    timeout = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Makes sure the intake running at the proper speed.
   */
  @Override
  public void execute() {
    this.intake.setIntakeSpeed();
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Returns true once the cargo is gone plus the timeout period.
   */
  @Override
  public boolean isFinished() {
    if (timeout != 0) {
      return (System.currentTimeMillis() - this.timeout > 1000);
    } else if (!this.intake.isCargoAtSensor()) {
      timeout = System.currentTimeMillis();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Makes sure the intake is stopped.
   */
  @Override
  public void end(boolean interrupted) {
    this.intake.stopIntakeMotor();
  }
}
