package frc.robot.subsystems.shooter.triggers;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.devices.RobotCargoCount;

/**
 * This trigger evaluates to true when the {@link RobotCargoCount} is 0; that
 * is, it is time to idle the shooter.
 */
public class ShooterIdleTrigger extends Trigger {
    private final RobotCargoCount robotCargoCount;

    /**
     * Creates a trigger for the given cargo counter.
     * 
     * @param robotCargoCount the cargo counter to track.
     */
    public ShooterIdleTrigger(RobotCargoCount robotCargoCount) {
        this.robotCargoCount = robotCargoCount;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This trigger is true when the cargo count is 0.
     */
    @Override
    public boolean get() {
        return this.robotCargoCount.get() == 0;
    }
}
