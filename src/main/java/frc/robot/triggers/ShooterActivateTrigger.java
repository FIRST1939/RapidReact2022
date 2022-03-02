package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;

public class ShooterActivateTrigger extends Trigger {

    private final Indexer indexer;

    public ShooterActivateTrigger (Indexer indexer) {

        this.indexer = indexer;
    }

    @Override
    public boolean get () {

        return this.indexer.isCargoAtSensor();
    } 
}
