package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Shooter;

/**
 * This command is designed to be the default command for the
 * shooter subsystem. It is used to shoot cargo during telop
 * with driver input of shooting rings.
 */
// TODO indexer element, dependency, and incorporation
public class SetShot extends CommandBase {

    private final Shooter shooter;
    private final Constants.SHOTS shot;

    /**
     * @param shooter the shooter subsystem used by this command
     * @param shot    the shot type that we are preparing.
     */
    public SetShot (final Shooter shooter, final Constants.SHOTS shot) {

        this.shooter = shooter;
        this.shot = shot;

        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(this.shooter);
    }

    @Override
    public void initialize () { 

        this.shooter.cargoShot(this.shot); 
    }

    // Driving with driver input never ends unless interrupted.
    @Override
    public boolean isFinished () { 

        return true; 
    }
}
