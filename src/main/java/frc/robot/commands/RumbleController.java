package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;

public class RumbleController extends CommandBase {
    
    private final GenericHID controller;

    public RumbleController (final GenericHID controller) {

        this.controller = controller;
    }
   

    @Override
    public void initialize () {
        
        Timer.reset();
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
    }
    
    @Override
    public void end () {
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
    }

    @Override
    public boolean isFinished () {

        return Timer.get() >= 1.0;
    }
}
