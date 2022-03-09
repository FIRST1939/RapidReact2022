package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class SetHome extends CommandBase {
    
    private final Climber climber;

    public SetHome (final Climber climber) {

        this.climber = climber;

        addRequirements(this.climber);
    }

    @Override
    public void execute () {

        this.climber.setHome();
    }

    @Override
    public boolean isFinished () {

        return true;
    }
}
