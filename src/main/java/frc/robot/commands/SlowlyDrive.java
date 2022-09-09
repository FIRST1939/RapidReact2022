package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class SlowlyDrive extends CommandBase {

    private final DriveTrain driveTrain;

    public SlowlyDrive (final DriveTrain driveTrain) {
        
        this.driveTrain = driveTrain;
        this.addRequirements(this.driveTrain);
    }

    @Override
    public void execute () {

        this.driveTrain.arcadeDrive(0.3, 0.0, 0.0);
    }

    @Override
    public void end (boolean interrupted) {

        this.driveTrain.stop();
    }

    @Override
    public boolean isFinished () {

        return false;
    }
}
