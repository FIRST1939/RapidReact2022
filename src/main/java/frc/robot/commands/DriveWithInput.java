package frc.robot.commands;

import frc.robot.subsystems.DriveTrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveWithInput extends CommandBase {

  private final DriveTrain driveTrain;
  private final DoubleSupplier speedSupplier;
  private final DoubleSupplier rotationSupplier;
  private final DoubleSupplier sidewindSupplier;

  /**
   * @param driveTrain              the drive train subsystem used by this
   *                                command.
   * @param speedSupplier           a supplier for changing speed input. Often
   *                                derived from joystick input but does not have
   *                                to be.
   * @param rotationSupplier        a supplier for changing rotational input.
   *                                Often derived from joystick input but does not
   *                                have to be.
   * @param sidewinderSpeedSupplier a supplier for changing sidewinder input.
   *                                Often derived from joystick input but does not
   *                                have to be.
   */
  public DriveWithInput (final DriveTrain driveTrain, final DoubleSupplier speedSupplier, final DoubleSupplier rotationSupplier, final DoubleSupplier sidewinderSpeedSupplier) {

    this.driveTrain = driveTrain;
    this.speedSupplier = speedSupplier;
    this.rotationSupplier = rotationSupplier;
    this.sidewindSupplier = sidewinderSpeedSupplier;

    this.addRequirements(this.driveTrain);
  }

  @Override
  public void initialize () {}

  @Override
  public void execute () {

    this.driveTrain.arcadeDrive(
      this.speedSupplier.getAsDouble(),
      this.rotationSupplier.getAsDouble(),
      this.sidewindSupplier.getAsDouble()
    );
  }
  
  /**
   * Driving with driver input never ends unless interrupted.
   */
  @Override
  public boolean isFinished () {

    return false;
  }

  @Override
  public void end (boolean interrupted) {
    
    this.driveTrain.stop();
  }
}
