package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class CancelClimb extends CommandBase {
    
    private final CommandBase commandOne;
    private final CommandBase commandTwo;

    public CancelClimb (final CommandBase commandOne, final CommandBase commandTwo) {

        this.commandOne = commandOne;
        this.commandTwo = commandTwo;
    }

    @Override
    public void execute () {

        this.commandOne.cancel();
        this.commandTwo.cancel();
    }

    @Override
    public boolean isFinished () {

        return true;
    }
}
