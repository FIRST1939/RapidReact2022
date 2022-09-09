package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.RobotCargoCount;

/**
 * This is the command to schedule when the button to extend the intake (start
 * gathering) is pressed. It will handle three cases.
 * 
 * <ol>
 * <li>If in manual mode, just extend.
 * <li>If automated, intake empty, and robot not full, start gathering.
 * <li>If automated and in any other state, ignore.
 * </ol>
 */
public class IntakeExtendCommandSelector extends CommandBase {

  private final Intake intake;
  private final ManualIntakeExtend manualExtend;

  public IntakeExtendCommandSelector (final Intake intake) {

    this.intake = intake;
    this.manualExtend = new ManualIntakeExtend(this.intake);
  }

  @Override
  public void initialize () {

    if (this.intake.isManualMode()) {

      this.manualExtend.schedule();
    } else if (!this.intake.isCargoAtSensor() && !RobotCargoCount.getInstance().isFull()) {

      IntakeGatheringEmptyState.getInstance(this.intake).schedule();
    }
  }

  @Override
  public boolean isFinished () {
    
    return true;
  }
}
