package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

import java.util.function.IntSupplier;

/**
 * This command is designed to be the default command for the
 * shooter subsystem. It is used to shoot cargo during telop
 * with driver input of shooting rings.
 */
// TODO indexer element, dependency, and incorporation
public class ShootWithInput extends CommandBase {

    private final Shooter shooter;
    private final IntSupplier distanceSupplier;

    /**
     * @param distanceSupplier a supplier for changing shooting rings.
     *                         often derived from joystick input but does not
     *                         have to be.
     */
    public ShootWithInput (final IntSupplier distanceSupplier) {

        this.shooter = Shooter.getInstance();
        this.distanceSupplier = distanceSupplier;

        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(this.shooter);
    }

    @Override
    public void execute () { this.shooter.cargoShot(this.distanceSupplier.getAsInt()); }

    // Driving with driver input never ends unless interrupted.
    @Override
    public boolean isFinished () { return false; }

    // Called once the command ends or is interrupted.
    @Override
    public void end (boolean interrupted) { this.shooter.stop(); }
}