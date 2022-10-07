package frc.robot.commands.auto;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

public class DriveTurnToRelativeAngle extends PIDCommand {

  private final DriveTrain driveTrain;

  /** Creates a new DriveTurnToRelativeAngle. */
  public DriveTurnToRelativeAngle (final DoubleSupplier relativeAngle, final DriveTrain driveTrain) {

    super(
        new PIDController(0.02, 0.0, 0.0),

        // Close loop on heading (adjusted yaw)
        driveTrain::getHeading,
        // Set reference to target
        relativeAngle,
        // Pipe output to turn robot
        output -> useOutput(driveTrain, output),
        // Require the drive
        driveTrain
    );

    // Set the controller to be continuous (because it is an angle controller)
    this.getController().enableContinuousInput(-180, 180);

    // Set the controller tolerance - the delta tolerance ensures the robot is
    // stationary at the setpoint before it is considered as having reached the
    // reference
    this.getController().setTolerance(Constants.DRIVE_AUTO_TURN_TO_ANGLE_TOL_DEG, Constants.DRIVE_AUTO_TURN_TO_ANGLE_TURN_RATE_TOL_DEG_PER_SEC);

    this.driveTrain = driveTrain;
  }

  @Override
  public void initialize () {

    super.isScheduled();
    this.driveTrain.resetDistance();
    this.driveTrain.resetHeading();
  }

  @Override
  public boolean isFinished () {

    // End when the controller is at the reference.
    return getController().atSetpoint();
  }

  private static void useOutput (final DriveTrain dt, final double pidControllerOutput) {

    double output = (pidControllerOutput >= 0) ? pidControllerOutput + Constants.DRIVE_AUTO_TURN_TO_ANGLE_KF : pidControllerOutput - Constants.DRIVE_AUTO_TURN_TO_ANGLE_KF;
    dt.arcadeDrive(0.0, output, 0.0);
  }
}
