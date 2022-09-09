package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class ManualIntakeExtend extends CommandBase {

  private final Intake intake;

  public ManualIntakeExtend (final Intake intake) {

    this.intake = intake;
  }

  @Override
  public void initialize () {

    this.intake.extendIntake();
  }

  @Override
  public boolean isFinished () {
    
    return true;
  }
}
