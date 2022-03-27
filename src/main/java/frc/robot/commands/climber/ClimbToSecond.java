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
            new LightsUpdater(LEDMode.FIRE),
            new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.offBar),
            //new WaitCommand(.2),
            new ParallelCommandGroup(
                new SetPiston(this.climber, (Boolean) true),
                new GetToPosition(this.climber, null, Constants.CLIMBER_POSITIONS.full)
            ),
            
            //new WaitCommand(.5),
           
            new WaitCommand(.2),
            new SetPiston(this.climber, (Boolean) false),
            new WaitCommand(1.5),
            new GetToPosition(this.climber, rumbleController, Constants.CLIMBER_POSITIONS.bottom)
        );
    }
}
