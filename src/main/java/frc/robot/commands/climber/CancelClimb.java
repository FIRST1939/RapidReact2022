package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Climber;

public class CancelClimb extends CommandBase {

    private final Climber climber;;

    public CancelClimb (final Climber climber) {
        this.climber = climber;
    }

    @Override
    public void initialize () {
        climber.setIsCommandRunning(false);
    }

    @Override
    public boolean isFinished () {

        return true;
    }
}
