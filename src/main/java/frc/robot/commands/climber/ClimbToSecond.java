package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.LightsUpdater;
import frc.robot.commands.RumbleController;
import frc.robot.subsystems.Climber;

public class ClimbToSecond extends SequentialCommandGroup {
    
    private final Climber climber;

    public ClimbToSecond (final Climber climber, final RumbleController rumbleController) {

        this.climber = climber;

        addCommands(
            new GetToPosition(this.climber, rumbleController, Constants.CLIMBER_POSITIONS.bottom)
        );
    }
}
