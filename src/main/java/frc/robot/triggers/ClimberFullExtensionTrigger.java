package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ClimberFullExtensionTrigger extends Trigger {
    
    private final JoystickButton joystickButton;
    private final JoystickButton joystickTriger;

    public ClimberFullExtensionTrigger (JoystickButton joystickButton, JoystickButton joystickTrigger) {

        this.joystickButton = joystickButton;
        this.joystickTriger = joystickTrigger;
    }

    @Override
    public boolean get () {

        return this.joystickButton.getAsBoolean() && this.joystickTriger.getAsBoolean();
    }
}
