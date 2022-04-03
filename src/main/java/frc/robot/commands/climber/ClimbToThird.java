package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.LightsUpdater;
import frc.robot.commands.RumbleController;
import frc.robot.subsystems.Climber;

public class ClimbToThird extends SequentialCommandGroup {

  private final Climber climber;

  public ClimbToThird(final Climber climber, final RumbleController rumbleController) {
    this.climber = climber;

    addCommands(
      new SetPiston(this.climber, (Boolean) false),
      new WaitCommand(.5),
      new GetToPosition(this.climber, rumbleController, Constants.CLIMBER_POSITIONS.finalBarRetract)
    );
  }
}
