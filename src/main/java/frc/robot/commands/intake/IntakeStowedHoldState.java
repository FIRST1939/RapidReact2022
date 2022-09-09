package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.RobotCargoCount;

public class IntakeStowedHoldState extends CommandBase {

  private static IntakeStowedHoldState INSTANCE;
  private final Intake intake;

  public static final synchronized IntakeStowedHoldState getInstance (final Intake intake) {

    if (INSTANCE == null) {

      INSTANCE = new IntakeStowedHoldState(intake);
    }

    return INSTANCE;
  }

  private IntakeStowedHoldState (final Intake intake) {

    this.intake = intake;
    this.addRequirements(this.intake);
  }

  @Override
  public void initialize() {

    this.intake.retractIntake();
    this.intake.stopIntakeMotor();
    Lights.getInstance().setColor(LEDMode.STROBE);
  }

  @Override
  public boolean isFinished () {

    return !RobotCargoCount.getInstance().isFull();
  }

  @Override
  public void end (boolean interrupted) {

    if (!this.intake.isManualMode()) {
      
      PostLoopCommandScheduler.addCommandToSchedule(IntakeStowedSendState.getInstance(this.intake));
    }
  }
}
