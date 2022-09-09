package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

/**
 * This command returns the shooter to the state of the last shot set via
 * {@link SetShot}. Typically used to exit the shooter idle state.
 */
public class ReturnToPriorShot extends CommandBase {

  private final Shooter shooter;

  /**
   * @param shooter the shooter being controlled. This instant command will not
   *                require the shooter.
   */
  public ReturnToPriorShot (final Shooter shooter) {

    this.shooter = shooter;
  }

  @Override
  public void initialize () {

    this.shooter.cargoShot();
  }

  @Override
  public boolean isFinished () {
    
    return true;
  }
}
