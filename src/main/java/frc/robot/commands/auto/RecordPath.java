package frc.robot.commands.auto;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class RecordPath extends CommandBase {

    private final DriveTrain driveTrain;
    private final Timer timer = new Timer();
    private final double timeStep = .15;

    private final Map<Double, Double> leftSteps = new HashMap<Double, Double>();
    private final Map<Double, Double> rightSteps = new HashMap<Double, Double>();
    
    public RecordPath (final DriveTrain driveTrain) {

        this.driveTrain = driveTrain;

        addRequirements(this.driveTrain);
    }

    @Override
    public void initialize () {

        this.driveTrain.resetDistance();
        this.timer.reset();
        this.timer.start();

        System.out.println("Recording current path.");
    }

    @Override
    public void execute () {

        double time = this.timer.get();

        if (time >= this.timeStep) {

            this.leftSteps.put(time, this.driveTrain.getLeftEncoderClicks());
            this.rightSteps.put(time, this.driveTrain.getRightEncoderClicks());

            System.out.println(this.driveTrain.getLeftEncoderClicks());

            this.driveTrain.resetDistance();
            this.timer.reset();

        }
    }
    
    @Override
    public boolean isFinished () {

        return false;
    }

    @Override
    public boolean runsWhenDisabled () {

        return true;
    }
}
