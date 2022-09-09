package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Limelight;
import frc.robot.Constants.LEDMode;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Lights;

public class ManualTurnToTarget extends CommandBase {

  private final DriveTrain driveTrain;
  private final Limelight limelight;

  private double angle;
  private int direction = 0;

  private int pipeline;

  private final RumbleController rumbleController;

  public ManualTurnToTarget (final DriveTrain driveTrain, final Limelight limelight, final int pipeline, final RumbleController rumbleController) {

    this.driveTrain = driveTrain;
    this.limelight = limelight;
    this.pipeline = pipeline;
    this.rumbleController = rumbleController;

    this.addRequirements(driveTrain);
  }

  @Override
  public void initialize () {

    Lights.getInstance().setColor(LEDMode.RED);
    this.angle = limelight.getHorizontalAngleError();
    this.limelight.setPipeline(pipeline);
    
    if (this.angle < -1) {

      this.direction = -1;
    } else if (angle > 1) {

      this.direction = 1;
    }
  }

  @Override
  public void execute () {

    this.angle = limelight.getHorizontalAngleError();

    if (Math.abs(this.angle) > 60) {

      this.driveTrain.arcadeDrive(0, 0.70 * this.direction, 0);
    } else if (Math.abs(this.angle) > 30) {

      this.driveTrain.arcadeDrive(0, 0.55 * this.direction, 0);
    } else {

      this.driveTrain.arcadeDrive(0, 0.45 * this.direction, 0);
    }
  }

  @Override
  public void end (boolean interrupted) {

    this.driveTrain.arcadeDrive(0, 0, 0);
    Lights.getInstance().setColor(LEDMode.GREEN);

    if (this.rumbleController != null) {

      this.rumbleController.schedule();
    }
  }

  @Override
  public boolean isFinished () {

    return (this.angle < 1 && this.angle > -1);
  }
}
