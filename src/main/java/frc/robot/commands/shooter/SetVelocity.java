package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

public class SetVelocity extends CommandBase {
    
    private final Shooter shooter;
    private final int velocity;

    /**
     * @param shooter the shooter subsystem used by this command
     */
    public SetVelocity (final Shooter shooter, final int velocity) {

        this.shooter = shooter;
        this.velocity = velocity;
    }

    @Override
    public void initialize () { 

        this.shooter.setVelocity(this.velocity); 
    }

    @Override
    public boolean isFinished () { 

        return true; 
    }
}
