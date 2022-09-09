package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

/**
 * Use this command during autonomous to drive a straight line for a distance
 * passed to the constructor.
 */
public class DriveStraightDistanceNoStop extends CommandBase {

  private final boolean forward;
  private final double absInches;
  // Divide absInches into accel, cruise, and decel sections.
  //private final double[] sectionAbsInches = new double[3];
  private final DriveTrain driveTrain;

  private final double power;

  /**
   * @param inches     the inches to drive. The intake end is forward (positive).
   *                   Pass a negative value for backwards.
   * @param driveTrain the drive train being controlled.
   */
  public DriveStraightDistanceNoStop (final double inches, final DriveTrain driveTrain, final double power) {

    this.forward = inches >= 0.0;
    this.absInches = Math.abs(inches);
    this.power = -power;
    //computeSections();
    this.driveTrain = driveTrain;
    this.addRequirements(this.driveTrain);
  }

  @Override
  public void initialize () {

    this.driveTrain.resetDistance();
    this.driveTrain.resetHeading();
  }

  @Override
  public void execute () {

    double turningValue = (-this.driveTrain.getHeading()) * Constants.DRIVE_AUTO_GYRO_STRAIGHT_KP;

    // Invert the direction of the turn if we are going backwards
    if (forward) {

      turningValue *= -1;
    }

    // double distSoFar = this.driveTrain.getDistance();
    
    /*
    if ((distSoFar <= this.sectionAbsInches[0])
        || (distSoFar > (this.sectionAbsInches[0] + this.sectionAbsInches[1]))) {
      power = power / 2.0;
    }
    */
    
    this.driveTrain.arcadeDrive(forward ? power : -power, turningValue, 0.0);
  }

  @Override
  public boolean isFinished () {

    return Math.abs(driveTrain.getDistance()) >= this.absInches;
  }

  @Override
  public void end (boolean interrupted) {
    
    //this.driveTrain.stop();
  }

  /*
  private void computeSections() {
    // Start with accel and decel taking 20% each.
    double accelDecelLen = this.absInches * 0.2;
    // Clamp to at least 8 inches and at most 16 inches.
    accelDecelLen = MathUtil.clamp(accelDecelLen, 8.0, 16.0);
    // Make sure a low clamp does not extend the total distance.
    accelDecelLen = accelDecelLen * 2.0 <= this.absInches
        ? accelDecelLen
        : this.absInches / 2.0;
    this.sectionAbsInches[0] = accelDecelLen;
    this.sectionAbsInches[1] = absInches - accelDecelLen - accelDecelLen;
    this.sectionAbsInches[2] = accelDecelLen;
  }*/
}
