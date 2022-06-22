package frc.robot.commands.util;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/**
 * When scheduled, an instance of this command will rumble the given controller
 * until canceled. Instances should race with an instance of
 * {@link WaitCommand}. See {@link Command#raceWith(Command...)} for the best
 * way to do this.
 */
public class RumbleController extends CommandBase {
    private final GenericHID controller;

    /**
     * @param controller the controller (XBox, Gamepad, etc) to rumble.
     */
    public RumbleController(final GenericHID controller) {
        this.controller = controller;
    }

    /**
     * Turn the rumble on when first scheduled.
     */
    @Override
    public void initialize() {
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
    }

    /**
     * Turn the rumble off when ending.
     */
    @Override
    public void end(boolean interrupted) {
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
    }
}
