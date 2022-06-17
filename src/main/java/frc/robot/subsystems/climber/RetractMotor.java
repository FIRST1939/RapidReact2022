package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RetractMotor extends CommandBase {
    
    private final Climber climber;

    public RetractMotor (final Climber climber) {

        this.climber = climber;

        addRequirements(this.climber);
    }

    @Override
    public void execute () {

        this.climber.setMotor(-5000);
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
    }
}
