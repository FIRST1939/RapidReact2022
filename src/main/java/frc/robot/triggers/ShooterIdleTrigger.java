package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.RobotCargoCount;

public class ShooterIdleTrigger extends Trigger {
    @Override
    public boolean get() {
        return RobotCargoCount.getInstance().isEmpty();
    }
}
