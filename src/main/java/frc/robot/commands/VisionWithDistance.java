package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Limelight;
import frc.robot.Constants.LEDMode;
import frc.robot.Constants.SHOTS;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.Shooter;

public class VisionWithDistance extends CommandBase {

  private final Shooter shooter;
  private final Limelight limelight;

  private double dy;
  private double velocity;
  private double lastVelocitySentToShooter;

  public VisionWithDistance (final Shooter shooter, final Limelight limelight) {

    this.shooter = shooter;
    this.limelight = limelight;
  }

  @Override
  public void initialize () {

    this.lastVelocitySentToShooter = 0;
  }

  @Override
  public void execute () {

    if (this.shooter.getShot() == SHOTS.visionTracked) {

      if (this.limelight.isTargetFound()) {

        // TODO Put the proper velocity function in here.
        this.dy = this.limelight.getVerticalAngleError();
        this.velocity = this.dy * Constants.VISION_M + Constants.VISION_B;

        // I have no idea if 2% change is the right amount of change
        // to trigger setting the velocity and restarting the periodic
        // check to see if the shooter it ready. But it is half the
        // current error checked in the shooter, so seemed like a
        // reasonable first try. If it still does not shoot, relax
        // this a bit more. Perhaps 3% or 4% (same as error tolerance).
        if (Math.abs(this.lastVelocitySentToShooter - velocity) > this.lastVelocitySentToShooter * 0.02) {

          this.shooter.cargoShot((int) velocity, true);
          this.lastVelocitySentToShooter = velocity;
        }
      } else {

        Lights.getInstance().setColor(LEDMode.RED);
      }
    }
  }
  
  @Override
  public void end (boolean interrupted) {

    Lights.getInstance().setColor(LEDMode.CONFETTI);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished () {
    
    return this.shooter.getShot() != SHOTS.visionTracked;
  }
}
