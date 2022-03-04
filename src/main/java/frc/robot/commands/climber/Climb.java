package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class Climb extends SequentialCommandGroup {
    
    private final Climber climber;

    public Climb (final Climber climber) {

        this.climber = climber;

        // TODO climbing process
        addCommands(
            new RetractMotor(this.climber),
            new SetPiston(this.climber, (Boolean) false),
            new ExtendMotor(this.climber, Constants.CLIMBER_EXTENSIONS.partialExtend),
            
            // Holds the double solenoid in the final position on the last bar.
            new SetPiston(this.climber, (Boolean) null)
        );
    }
}
