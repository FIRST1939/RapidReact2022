package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class TurnToAngle extends CommandBase {

  private double angle;
  private double target;
  private int direction = 0;

  private double distance;

  private final DriveTrain driveTrain;

  public TurnToAngle (final DriveTrain driveTrain, final double target) {

    this.driveTrain = driveTrain;
    this.target = target;
    this.addRequirements(driveTrain);
  }

  @Override
  public void initialize () {

    this.driveTrain.resetHeading();
    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    if (this.distance < -1) {

      this.direction = -1;
    } else if (this.distance > 1) {

      this.direction = 1;
    }
  }

  @Override
  public void execute () {

    this.angle = this.driveTrain.getHeading();
    this.distance = this.target - this.angle;

    /*
    System.out.println("Angle: " + this.angle);
    System.out.println("Target: " + this.target);
    System.out.println("Distance: " + this.distance);
    */

    if (Math.abs(this.distance) > 31) {

      this.driveTrain.arcadeDrive(0, 0.75 * this.direction, 0);
    } else if (Math.abs(this.distance) > 15) {

      this.driveTrain.arcadeDrive(0, 0.6 * this.direction, 0);
    } else {

      this.driveTrain.arcadeDrive(0, 0.48 * this.direction, 0);
    }
  }

  @Override
  public void end (boolean interrupted) {

    this.driveTrain.arcadeDrive(0, 0, 0);
  }

  @Override
  public boolean isFinished () {

    if ((this.distance / this.direction) < 0) return true;

    this.distance = this.target - this.angle;
    return (this.distance < .75 && this.distance > -0.75);
  }
}
