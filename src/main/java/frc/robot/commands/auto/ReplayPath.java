package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
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
    public void execute () {

        double time = this.timer.get();

        if (time >= this.timeStep) {

            this.driveTrain.leftGroup.set(this.driveTrain.leftSteps.get(this.index));
            this.driveTrain.rightGroup.set(this.driveTrain.rightSteps.get(this.index));

            this.timer.reset();

        }
    }

    @Override
    public boolean isFinished () {

        return index == this.driveTrain.leftSteps.size();
    }
}
