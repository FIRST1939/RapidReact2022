package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotContainer;

public class ReplayPathTrigger extends Trigger{

    private final RobotContainer robotContainer;
    private final JoystickButton joystickButton;

    public ReplayPathTrigger (RobotContainer robotContainer, JoystickButton joystickButton) {

        this.robotContainer = robotContainer;
        this.joystickButton = joystickButton;
    }

    @Override
    public boolean get () {

        return this.joystickButton.get();
    }
}
