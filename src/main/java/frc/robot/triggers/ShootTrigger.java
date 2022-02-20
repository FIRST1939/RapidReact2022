package frc.robot.triggers;

import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

public class ShootTrigger extends Trigger {

  private final Indexer indexer;
  private final Shooter shooter;
  private final Button button;
  
  public ShootTrigger(Indexer indexer, Shooter shooter, Button button) {

    this.indexer = indexer;
    this.shooter = shooter;
    this.button =  button;
  }

  @Override
  public boolean get() {
    
    return indexer.isCargoAtSensor() && shooter.isShooterReady() && button.get();
  }
}
