package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.RobotCargoCount;

public class IntakeAtSensorState extends CommandBase {

  private static IntakeAtSensorState INSTANCE;
  private final Intake intake;
  private long startTime;

  public static final synchronized IntakeAtSensorState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeAtSensorState(intake);
    }

    return INSTANCE;
  }

  private IntakeAtSensorState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(intake);
  }

  public void initialize () {

    this.startTime = System.currentTimeMillis();
  }

  public void execute () {

    this.intake.setIntakeSpeed();
  }

  @Override
  public boolean isFinished () {

    return System.currentTimeMillis() - this.startTime >= Constants.INTAKE_AT_SENSOR_TIME_ADJUSTMENT_MS;
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.intake.isManualMode()) {

      if (RobotCargoCount.getInstance().isFull()) {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedHoldState.getInstance(this.intake));
      } else if (interrupted) {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedSendState.getInstance(this.intake));
      } else {

        PostLoopCommandScheduler.addCommandToSchedule(IntakeGatheringSendState.getInstance(this.intake));
      }
    }
  }
}
