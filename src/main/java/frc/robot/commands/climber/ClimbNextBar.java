package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commands.RumbleController;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Lights;

public class ClimbNextBar extends SequentialCommandGroup {
    private final Climber climber;

    public ClimbNextBar(final Climber climber, final RumbleController rumbleController) {
        this.climber = climber;

        addCommands(
                new InstantCommand(() -> Lights.getInstance().setColor(Constants.LEDMode.FIRE)),
                new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.offBar),
                new SetPiston(this.climber, (Boolean) true),
                new WaitCommand(0.5),
                new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.full));
    }
}
