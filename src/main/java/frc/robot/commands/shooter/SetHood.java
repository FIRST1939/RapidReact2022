package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

/**
 * This command is designed to be the manual override command for the
 * shooter subsystem. It is used to toggle the hood when the
 * default enumerations are malfunctioning.
 */
public class SetHood extends CommandBase {
    
    private final Shooter shooter;
    private final Boolean hood;

    /**
     * @param shooter the shooter subsystem used by this command
     * @param hood    true to extend hood, false to retract
     */
    public SetHood (final Shooter shooter, final boolean hood) {
        
        this.shooter = shooter; 
        this.hood = hood;
    }
    
    @Override 
    public void initialize () {

        this.shooter.setHood(this.hood);
    }

    // Driving with driver input never ends unless interrupted.
    @Override
    public boolean isFinished () { 

        return true; 
    }
}
