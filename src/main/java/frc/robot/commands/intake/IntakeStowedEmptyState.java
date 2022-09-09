package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.Intake;

public class IntakeStowedEmptyState extends CommandBase {

  private static IntakeStowedEmptyState INSTANCE;
  private final Intake intake;

  public static final synchronized IntakeStowedEmptyState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeStowedEmptyState(intake);
    }

    return INSTANCE;
  }

  public IntakeStowedEmptyState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(this.intake);
  }

  @Override
  public void initialize () {

    this.intake.retractIntake();
    this.intake.stopIntakeMotor();
  }

  @Override
  public boolean isFinished () {

    return true;
  }
}
