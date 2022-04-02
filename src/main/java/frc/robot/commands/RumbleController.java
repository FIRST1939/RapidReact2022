package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;

public class RumbleController extends CommandBase {
    
    private static RumbleController rumbleControllerInstance = null;

    private final GenericHID controller;
    private final Timer timer = new Timer();

    private RumbleController (final GenericHID controller) {

        this.controller = controller;
    }
   
    /**
     * @return the current instance of the RumbleController command
     */
    public static final synchronized RumbleController getInstance (final GenericHID controller) {

        if (rumbleControllerInstance == null) { rumbleControllerInstance = new RumbleController(controller); }
        return rumbleControllerInstance;
    }

    @Override
    public void initialize () {
        
        this.timer.reset();
        this.timer.start();
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
    }
    
    @Override
    public void end (boolean interrupted) {
        
        this.timer.stop();
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
    }

    @Override
    public boolean isFinished () {

        return this.timer.get() >= 1.0;
    }
}
