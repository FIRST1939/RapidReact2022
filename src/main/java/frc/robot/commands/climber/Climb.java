package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class Climb extends SequentialCommandGroup {
    
    private final Climber climber;

    public Climb (final Climber climber) {

        this.climber = climber;

        // TODO climbing process
        addCommands(
            new ExtendMotor(this.climber, Constants.CLIMBER_EXTENSIONS.partialExtend),
            new WaitCommand(.5),
            new SetPiston(this.climber, (Boolean) true),
            new WaitCommand(.5),
            new ExtendMotor(this.climber, Constants.CLIMBER_EXTENSIONS.fullExtend),
            new WaitCommand(.5),
            new SetPiston(this.climber, (Boolean) false),
            new WaitCommand(1.5),
            new RetractMotor(this.climber)
        );
    }
}
