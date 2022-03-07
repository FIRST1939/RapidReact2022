package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class RetractMotor extends CommandBase {
    
    private final Climber climber;

    public RetractMotor (final Climber climber) {

        this.climber = climber;

        addRequirements(this.climber);
    }

    @Override
    public void execute () {

        this.climber.setMotor(-Constants.CLIMBER_EXTENSION_VELOCITY);
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
    }

    @Override
    public boolean isFinished () {

        //return this.climber.isMotorFullyRetracted();
        return false;
    }
}
