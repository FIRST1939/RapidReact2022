package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.RobotCargoCount;

public class IntakeGatheringEmptyState extends CommandBase {

  private static IntakeGatheringEmptyState INSTANCE;
  private final Intake intake;

  public static final synchronized IntakeGatheringEmptyState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeGatheringEmptyState(intake);
    }

    return INSTANCE;
  }

  public IntakeGatheringEmptyState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(this.intake);
  }

  @Override
  public void initialize () {

    this.intake.extendIntake();
    Lights.getInstance().setColor(LEDMode.PINK);
  }

  @Override
  public void execute () {

    this.intake.setIntakeSpeed();
  }

  @Override
  public boolean isFinished () {

    return this.intake.isCargoAtSensor();
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.intake.isManualMode()) {

      if (!interrupted) {
        RobotCargoCount.getInstance().increment();
        PostLoopCommandScheduler.addCommandToSchedule(IntakeAtSensorState.getInstance(this.intake));
      } else {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedEmptyState.getInstance(this.intake));
      }
    }
  }
}
