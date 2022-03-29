package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class RecordPath extends CommandBase {

    private final DriveTrain driveTrain;
    private final Timer timer = new Timer();
    private final double timeStep = .1;
    
    public RecordPath (final DriveTrain driveTrain) {

        this.driveTrain = driveTrain;
        this.driveTrain.leftSteps.clear();
        this.driveTrain.rightSteps.clear();

        addRequirements(this.driveTrain);
    }

    @Override
    public void initialize () {

        this.driveTrain.coastMode(true);
        this.driveTrain.resetDistance();
        this.timer.reset();
        this.timer.start();

        System.out.println("Recording current path.");
    }

    @Override
    public void execute () {

        double time = this.timer.get();

        if (time >= this.timeStep) {

            this.driveTrain.leftSteps.add(this.driveTrain.getLeftEncoderClicks());
            this.driveTrain.rightSteps.add(this.driveTrain.getRightEncoderClicks());

            System.out.println("Left Encoder Clicks: " + this.driveTrain.getLeftEncoderClicks());
            System.out.println("Right Encoder Clicks: " + this.driveTrain.getRightEncoderClicks());

            this.driveTrain.resetDistance();
            this.timer.reset();
        }
    }

    @Override
    public void end (boolean interrupted) {

        this.driveTrain.leftSteps.add(0.0);
        this.driveTrain.rightSteps.add(0.0);
        this.driveTrain.coastMode(false);
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
