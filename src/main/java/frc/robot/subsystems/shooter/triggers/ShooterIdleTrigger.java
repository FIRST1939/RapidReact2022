package frc.robot.subsystems.shooter.triggers;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.devices.RobotCargoCount;

public class ShooterIdleTrigger extends Trigger {
    private final RobotCargoCount robotCargoCount;

    public ShooterIdleTrigger(RobotCargoCount robotCargoCount) {
        this.robotCargoCount = robotCargoCount;
    }

    @Override
    public boolean get() {
        return this.robotCargoCount.get() == 0;
    }
}
