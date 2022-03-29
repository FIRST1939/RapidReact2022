package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

public class ReplayPath extends CommandBase {
    
    private final DriveTrain driveTrain;
    private final Timer timer = new Timer();
    private final double timeStep = .1;

    private int index = 0;

    public ReplayPath (final DriveTrain driveTrain) {

        this.driveTrain = driveTrain;

        addRequirements(this.driveTrain);
    }

    @Override
    public void initialize () {

        this.timer.reset();
        this.timer.start();
    }

    @Override
    public void execute () {

        double time = this.timer.get();
        this.driveTrain.diffDrive.tankDrive(this.driveTrain.leftSteps.get(this.index) / Constants.REPLAY_DIVISER, -1 * this.driveTrain.rightSteps.get(this.index) / Constants.REPLAY_DIVISER);

        if (time >= this.timeStep) {

            this.timer.reset();
            this.index++;
        }
    }

    @Override
    public boolean isFinished () {

        return index == (this.driveTrain.leftSteps.size() - 1);
    }

    @Override
    public boolean runsWhenDisabled () {

        return true;
    }
}
