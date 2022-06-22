package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ClimbNextBar extends SequentialCommandGroup {
    private final Climber climber;

    public ClimbNextBar(final Climber climber) {
        this.climber = climber;
        addCommands(
                new GetToPosition(this.climber, ClimberPositions.offBar),
                new SetPiston(this.climber, (Boolean) true),
                new WaitCommand(0.5),
                new GetToPosition(this.climber, ClimberPositions.full));
    }
}
