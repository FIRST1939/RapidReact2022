package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.LightsUpdater;
import frc.robot.commands.RumbleController;
import frc.robot.subsystems.Climber;

public class ClimbNextBar extends SequentialCommandGroup {
    
    private final Climber climber;

    public ClimbNextBar (final Climber climber, final RumbleController rumbleController) {

        this.climber = climber;

        addCommands(
            new LightsUpdater(LEDMode.FIRE),
            new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.offBar),
            //new WaitCommand(.2),
            new SetPiston(this.climber, (Boolean) true),
            new WaitCommand(0.5),
            new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.full),
            
            //new WaitCommand(.5),
           
            new WaitCommand(.2),
            new SetPiston(this.climber, (Boolean) false)
        );
    }
}
