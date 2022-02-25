package frc.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Climber;

public class ClimbMotorTrigger extends Trigger {
    
    private final Climber climber;
    private final BooleanSupplier joystickSupplier;

    public ClimbMotorTrigger (Climber climber, BooleanSupplier joystickSupplier) {

        this.climber = climber;
        this.joystickSupplier = joystickSupplier;
    }

    @Override
    public boolean get () {

        return climber.isPistonExtended() && joystickSupplier.getAsBoolean();
    }
}
