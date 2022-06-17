package frc.robot.subsystems.shooter.triggers;

import java.util.function.BooleanSupplier;

import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.shooter.Shooter;

/**
 * An instance of this class is used to make sure we shoot only when the shooter
 * has spun up to speed.
 */
public class ManualShootTrigger extends AbstractShootTrigger {
  private final Indexer indexer;

  /**
   * @param indexer         the {@link Indexer} for this robot.
   * @param shooter         the {@link Shooter} for this robot.
   * @param triggerSupplier a boolean supplier that returns true when the driver
   *                        is ready to shoot.
   */
  public ManualShootTrigger(Indexer indexer, Shooter shooter, BooleanSupplier triggerSupplier) {
    super(shooter, triggerSupplier);
    this.indexer = indexer;
  }

  /**
   * @return true when appropriate to shoot. The indexer, shooter, and driver all
   *         all ready.
   */
  @Override
  public boolean get() {
    return !indexer.isStateMachineRunning() && super.get();
  }
}
