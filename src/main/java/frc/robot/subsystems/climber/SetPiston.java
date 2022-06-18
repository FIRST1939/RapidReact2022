package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetPiston extends CommandBase {
    
    private final Climber climber;
    private final Boolean piston;

    public SetPiston (final Climber climber, final Boolean piston) {

        this.climber = climber;
        this.piston = piston;
    }

    @Override
    public void initialize () {

        this.climber.setPiston(this.piston);
    }

    @Override
    public boolean isFinished () {

        return true;
    }
}