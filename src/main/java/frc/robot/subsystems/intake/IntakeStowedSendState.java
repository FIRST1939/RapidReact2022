// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

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

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.intake.retractIntake();
    timeout = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    this.intake.setIntakeSpeed();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (timeout != 0) {
      return (System.currentTimeMillis() - this.timeout > 1000);
    } else if (!this.intake.isCargoAtSensor()) {
      timeout = System.currentTimeMillis();
    }
    return false;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.intake.stopIntakeMotor();
  }
}
