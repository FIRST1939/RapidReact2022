package frc.robot.triggers;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Timer;
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
  private boolean lastDriverTrigger = false;
  private final Timer shooterReadyTimeout = new Timer();

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
    final boolean currentDriverTrigger = triggerSupplier.getAsBoolean();
    boolean shooterTimedOut = false;
    if (currentDriverTrigger) {
      // The driver is now and may have been signaling ready.
      if (this.lastDriverTrigger) {
        // Drive was signaling ready before too.
        if (this.shooterReadyTimeout.get() >= 2.0) {
          shooterTimedOut = true;
        }
      } else {
        // Driver just transitioned to ready.
        this.shooterReadyTimeout.reset();
        this.shooterReadyTimeout.start();
      }
    } else {
      // Driver not signaling ready.
      this.shooterReadyTimeout.stop();
    }
    this.lastDriverTrigger = currentDriverTrigger;
    return (shooterTimedOut || shooter.isShooterReady()) && currentDriverTrigger;
  }
}
