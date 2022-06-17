package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.subsystems.climber.Climber;

public class ClimbToThird extends SequentialCommandGroup {

  private final Climber climber;

  public ClimbToThird(final Climber climber) {
    this.climber = climber;

    addCommands(
        new SetPiston(this.climber, (Boolean) false),
        new WaitCommand(1.5),
        new GetToPosition(this.climber, Constants.CLIMBER_POSITIONS.finalBarRetract));
  }
}
