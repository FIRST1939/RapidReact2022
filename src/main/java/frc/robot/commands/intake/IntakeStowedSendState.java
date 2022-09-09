package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Intake;

public class IntakeStowedSendState extends CommandBase {

  private static IntakeStowedSendState INSTANCE;
  private final Intake intake;
  private long timeout = 0;

  public static final synchronized IntakeStowedSendState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeStowedSendState(intake);
    }

    return INSTANCE;
  }

  private IntakeStowedSendState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(this.intake);
  }

  @Override
  public void initialize () {

    this.intake.retractIntake();
    this.timeout = 0;
  }

  @Override
  public void execute () {

    this.intake.setIntakeSpeed();
  }

  @Override
  public void end (boolean interrupted) {

    this.intake.stopIntakeMotor();

    if (!this.intake.isManualMode()) {

      PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedEmptyState.getInstance(this.intake));
    }
  }

  @Override
  public boolean isFinished () {

    if (this.timeout != 0) {

      return (System.currentTimeMillis() - this.timeout > 1000);
    } else if (!this.intake.isCargoAtSensor()){

      this.timeout = System.currentTimeMillis();
    }
    
    return false;
  }
}
