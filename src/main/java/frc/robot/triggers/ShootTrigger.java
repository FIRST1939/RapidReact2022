package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

public class ShootTrigger extends Trigger {

  private final Indexer indexer;
  private final Shooter shooter;
  private final BooleanSupplier triggerSupplier;
  
  public ShootTrigger(Indexer indexer, Shooter shooter, BooleanSupplier triggerSupplier) {

    this.indexer = indexer;
    this.shooter = shooter;
    this.triggerSupplier =  triggerSupplier;
  }

  @Override
  public boolean get() {
    
    return indexer.isCargoAtSensor() && shooter.isShooterReady() && triggerSupplier.getAsBoolean();
  }
}
