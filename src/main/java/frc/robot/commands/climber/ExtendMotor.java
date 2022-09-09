package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Climber;

public class ExtendMotor extends CommandBase {
    
    private final Climber climber;

    public ExtendMotor (final Climber climber) {

        this.climber = climber;
        this.addRequirements(this.climber);
    }

    @Override
    public void execute () {

        this.climber.setMotor(5000);
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
    }
}
