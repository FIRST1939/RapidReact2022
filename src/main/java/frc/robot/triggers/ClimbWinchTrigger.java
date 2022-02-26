package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Climber;

public class ClimbWinchTrigger extends Trigger {

    private final Climber climber;
    private final JoystickButton joystickButtonOne;
    private final JoystickButton joystickButtonTwo;

    public ClimbWinchTrigger (Climber climber, JoystickButton joystickButtonOne, JoystickButton joystickButtonTwo) {

        this.climber = climber;
        this.joystickButtonOne = joystickButtonOne;
        this.joystickButtonTwo = joystickButtonTwo;
    }

    @Override
    public boolean get () {

        return this.climber.isPistonExtended() & this.joystickButtonOne.getAsBoolean() && this.joystickButtonTwo.getAsBoolean();
    }
}
