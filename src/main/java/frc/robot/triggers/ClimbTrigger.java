package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Climber;

/**
 * This trigger confirms that the piston is extended,
 * and that the climber motor is fully extended,
 * before beginning the automated climb.
 */
public class ClimbTrigger extends Trigger {
    
    private final Climber climber;
    private final JoystickButton joystickButton;

    public ClimbTrigger (Climber climber, JoystickButton joystickButton) {

        this.climber = climber;
        this.joystickButton = joystickButton;
    }

    @Override
    public boolean get () {

        return this.climber.isPistonExtended() && this.climber.isMotorFullyExtended() && this.joystickButton.getAsBoolean();
    }
}
