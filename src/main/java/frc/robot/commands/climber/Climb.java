package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Climber;

public class Climb extends SequentialCommandGroup {
    
    private final Climber climber;

    public Climb (final Climber climber) {

        this.climber = climber;

        addCommands(
            new RetractMotor(this.climber),
            new SetPiston(this.climber, false),
            new ExtendMotor(this.climber)
        );

        addRequirements(this.climber);
    }
}
