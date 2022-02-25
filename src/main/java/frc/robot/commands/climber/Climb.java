package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Constants;
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
        for (int loop = 0; loop < 1; loop++) {

            this.climber.setMotor(-Constants.CLIMBER_EXTENSION_VELOCITY);
            new WaitUntilCommand(() -> this.climber.isMotorFullyRetracted());
            this.climber.setMotor(0);

            new SetPiston(this.climber, false);
            this.climber.setMotor(Constants.CLIMBER_EXTENSION_VELOCITY);
            new WaitUntilCommand(() -> this.climber.isMotorFullyExtended());
            this.climber.setMotor(0);
        }

        this.finished = true;
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
    }

    @Override
    public boolean isFinished () {

        return this.finished;
    }
}
