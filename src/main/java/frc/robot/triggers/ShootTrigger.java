package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

/**
 * An instance of this class is used to make sure we shoot only when the shooter
 * has spun up to speed and a cargo is available for shooting.
 */
public class ShootTrigger extends Trigger {
  private final Indexer indexer;
  private final Shooter shooter;
  private final BooleanSupplier triggerSupplier;

  /**
   * @param indexer         the {@link Indexer} for this robot.
   * @param shooter         the {@link Shooter} for this robot.
   * @param triggerSupplier a boolean supplier that returns true when the driver
   *                        is ready to shoot.
   */
  public ShootTrigger(Indexer indexer, Shooter shooter, BooleanSupplier triggerSupplier) {
    this.indexer = indexer;
    this.shooter = shooter;
    this.triggerSupplier = triggerSupplier;
  }

  /**
   * @return true when appropriate to shoot. The indexer, shooter, and driver all
   *         all ready.
   */
  @Override
  public boolean get() {
    return indexer.isCargoAtSensor() && shooter.isShooterReady() && triggerSupplier.getAsBoolean();
  }
}
