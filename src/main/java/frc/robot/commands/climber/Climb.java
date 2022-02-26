package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Climber;

public class Climb extends CommandBase {
    
    private final Climber climber;
    private boolean finished;

    public Climb (final Climber climber) {

        this.climber = climber;
        this.finished = false;

        addRequirements(this.climber);
    }

    @Override
    public void execute () {

        // TODO number of loops to run
        new RetractMotor(this.climber).schedule();
        new SetPiston(this.climber, false).schedule();
        new ExtendMotor(this.climber).schedule();

        this.finished = true;
    }

    @Override
    public boolean isFinished () {

        return this.finished;
    }
}