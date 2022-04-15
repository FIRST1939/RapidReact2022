package frc.robot.commands.shooter;

import java.util.function.IntSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

public class SetVelocity extends CommandBase {
    
    private final Shooter shooter;
    private final IntSupplier velocitySupplier;

    /**
     * @param shooter the shooter subsystem used by this command
     */
    public SetVelocity (final Shooter shooter, final IntSupplier velocitySupplier) {

        this.shooter = shooter;
        this.velocitySupplier = velocitySupplier;
    }

    @Override
    public void initialize () { 

        this.shooter.setVelocity(this.velocitySupplier.getAsInt()); 
    }

    @Override
    public boolean isFinished () { 

        return true; 
    }
}
