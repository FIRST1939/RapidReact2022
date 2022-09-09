package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class ManualIntakeRetract extends CommandBase {

  private final Intake intake;

  public ManualIntakeRetract (final Intake intake) {

    this.intake = intake;
  }

  @Override
  public void initialize () {

    this.intake.retractIntake();
  }

  @Override
  public boolean isFinished () {
    
    return true;
  }
}
