// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
  public ReturnToPriorShot(final Shooter shooter) {
    this.shooter = shooter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.shooter.cargoShot();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
