package frc.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Climber;

public class ClimbPistonTrigger extends Trigger {
    
    private final Climber climber;
    private final BooleanSupplier joystickSupplier;

    public ClimbPistonTrigger (Climber climber, BooleanSupplier joystickSupplier) {

        this.climber = climber;
        this.joystickSupplier = joystickSupplier;
    }

    @Override
    public boolean get () {

        return climber.isMotorFullyRetracted() && joystickSupplier.getAsBoolean();
    }
}
