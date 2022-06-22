package frc.robot.commands.util;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * An instance of this command will rumble the given controller for 1 second
 * when scheduled.
 */
public class RumbleController extends CommandBase {
    private final GenericHID controller;
    private final Timer timer = new Timer();

    /**
     * @param controller the controller (XBox, Gamepad, etc) to rumble.
     */
    public RumbleController(final GenericHID controller) {
        this.controller = controller;
    }

    @Override
    public void initialize() {
        this.timer.reset();
        this.timer.start();
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
    }

    @Override
    public void end(boolean interrupted) {
        this.timer.stop();
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
    }

    @Override
    public boolean isFinished() {
        return this.timer.get() >= 1.0;
    }
}
