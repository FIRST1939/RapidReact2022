package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.IndexerStateMachine.State;

/**
 * An instance of this class is used to make sure we shoot only when the shooter
 * has spun up to speed and a cargo is available for shooting.
 */
public class ShootTrigger extends AbstractShootTrigger {
  private final Indexer indexer;

  /**
   * @param indexer         the {@link Indexer} for this robot.
   * @param shooter         the {@link Shooter} for this robot.
   * @param triggerSupplier a boolean supplier that returns true when the driver
   *                        is ready to shoot.
   */
  public ShootTrigger(Indexer indexer, Shooter shooter, BooleanSupplier triggerSupplier) {
    super(shooter, triggerSupplier);
    this.indexer = indexer;
  }

  /**
   * @return true when appropriate to shoot. The indexer, shooter, and driver all
   *         all ready.
   */
  @Override
  public boolean get() {
    return indexer.getStateMachine().getCurrentState() == State.READY_TO_SHOOT
        && super.get();
  }
}
