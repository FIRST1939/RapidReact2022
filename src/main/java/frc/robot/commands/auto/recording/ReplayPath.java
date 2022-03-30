package frc.robot.commands.auto.recording;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

public class ReplayPath extends CommandBase {
    
    private final DriveTrain driveTrain;
    private final Timer timer = new Timer();
    private final double timeStep = .1;

    private ArrayList<Double> leftSteps;
    private ArrayList<Double> rightSteps;
    private int index = 0;

    public ReplayPath (final DriveTrain driveTrain, ArrayList<Double> leftSteps, ArrayList<Double> rightSteps) {

        this.driveTrain = driveTrain;
        this.leftSteps = leftSteps;
        this.rightSteps = rightSteps;

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
        this.driveTrain.diffDrive.tankDrive(this.leftSteps.get(this.index) / Constants.REPLAY_DIVISER, -1 * this.rightSteps.get(this.index) / Constants.REPLAY_DIVISER);

        if (time >= this.timeStep) {

            this.timer.reset();
            this.index++;
        }
    }

    @Override
    public boolean isFinished () {

        return index == (((this.leftSteps.size() + this.rightSteps.size()) / 2) - 1);
    }

    @Override
    public boolean runsWhenDisabled () {

        return true;
    }
}
