package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Shooter;

/**
 * This is an abstract base class for automated and manual mode shoot triggers.
 * This class makes sure we shoot only when the shooter has spun up to speed and
 * the driver is ready for shooting.
 * 
 * <p>
 * Subclasses MUST use super to combine this result with the particulars or
 * automated and manual triggering.
 * </p>
 */
public abstract class AbstractShootTrigger extends Trigger {
  private final Shooter shooter;
  private final BooleanSupplier triggerSupplier;

  /**
   * @param shooter         the {@link Shooter} for this robot.
   * @param triggerSupplier a boolean supplier that returns true when the driver
   *                        is ready to shoot.
   */
  public AbstractShootTrigger(final Shooter shooter, final BooleanSupplier triggerSupplier) {
    this.shooter = shooter;
    this.triggerSupplier = triggerSupplier;
  }

  /**
   * @return true when appropriate to shoot. The shooter and driver are ready.
   *         Subclasses MUST use super to combine this result with the particulars
   *         or automated and manual triggering.
   */
  @Override
  public boolean get() {
    return shooter.isShooterReady() && triggerSupplier.getAsBoolean();
  }
}
