package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class ExtendMotor extends CommandBase {
    
    private final Climber climber;
    private final Constants.CLIMBER_EXTENSIONS extension;

    public ExtendMotor (final Climber climber, final Constants.CLIMBER_EXTENSIONS extension) {

        this.climber = climber;
        this.extension = extension;

        addRequirements(this.climber);
    }

    @Override
    public void execute () {

        this.climber.setMotor(extension.velocity);
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
    }

    @Override
    public boolean isFinished () {

        return this.climber.getMotorPosition() >= extension.distance;
    }
}
