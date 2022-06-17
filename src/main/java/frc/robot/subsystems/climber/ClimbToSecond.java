package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;

public class ClimbToSecond extends SequentialCommandGroup {
    private final Climber climber;

    public ClimbToSecond(final Climber climber) {
        this.climber = climber;

        addCommands(
                new SetPiston(this.climber, (Boolean) false),
                new WaitCommand(1.5),
                new GetToPosition(this.climber, Constants.CLIMBER_POSITIONS.bottom));
    }
}
