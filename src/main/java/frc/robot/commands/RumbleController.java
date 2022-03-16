package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;

public class RumbleController extends CommandBase {
    
    private final GenericHID controller;
    private boolean finished = false;

    public RumbleController (final GenericHID controller) {

        this.controller = controller;
    }

    @Override
    public void execute (boolean interrupted) {
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
        
        Timer.delay(0.1);
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
      
        this.finished = true;
    }

    @Override
    public boolean isFinished () {

        return this.finished;
    }
}
