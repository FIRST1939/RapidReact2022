package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Intake;

public class IntakeGatheringSendState extends CommandBase {

  private static IntakeGatheringSendState INSTANCE;
  private final Intake intake;

  public static final synchronized IntakeGatheringSendState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeGatheringSendState(intake);
    }

    return INSTANCE;
  }

  private IntakeGatheringSendState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(this.intake);
  }

  @Override
  public void initialize () {

    this.intake.extendIntake();
  }

  public void execute () {

    this.intake.setIntakeSpeed();
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.intake.isManualMode()) {

      if (interrupted) {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedSendState.getInstance(this.intake));
      } else {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeGatheringEmptyState.getInstance(this.intake));
      }
    }
  }

  @Override
  public boolean isFinished () {
    
    return !this.intake.isCargoAtSensor();
  }
}
