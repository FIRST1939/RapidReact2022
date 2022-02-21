package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

/**
 * This command is designed to be the manual override command for the
 * shooter subsystem. It is used to toggle the hood when the
 * default enumerations are malfunctioning.
 */
public class ToggleHood extends CommandBase {
    
    private final Shooter shooter;

    /**
     * @param shooter the shooter subsystem used by this command
     */
    public ToggleHood (final Shooter shooter) {
        
        this.shooter = shooter; 

        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(this.shooter);
    }
    
    @Override 
    public void initialize () {

        this.shooter.setHood(!(this.shooter.isHoodUp()));
    }

    // Driving with driver input never ends unless interrupted.
    @Override
    public boolean isFinished () { 

        return true; 
    }
}
