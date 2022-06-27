package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;

/**
 * This command is used to configure the shooter for a particular shot (see
 * {@link Constants.Shots}). It can be tied to a button for teleop or made part
 * of an automode command group. Note that this does not cause a shot to be
 * fired.
 * 
 * TODO get rid of this class in favor of InstantCommand.
 */
public class SetShot extends CommandBase {
    private final Shooter shooter;
    private final Shots shot;

    /**
     * @param shooter the shooter subsystem used by this command
     * @param shot    the shot type that we are preparing.
     */
    public SetShot(final Shooter shooter, final Shots shot) {
        this.shooter = shooter;
        this.shot = shot;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation simply calls {@link Shooter#cargoShot(Shots)}.
     */
    @Override
    public void initialize() {
        this.shooter.cargoShot(this.shot);
    }

    /**
     * {@inheritDoc}
     * 
     * @return always returns true. This is an instant command.
     */
    @Override
    public boolean isFinished() {
        return true;
    }
}
