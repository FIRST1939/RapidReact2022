package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;
import frc.robot.subsystems.Climber;

public class GetToPosition extends CommandBase {
    
    private final Climber climber;
    private final XboxController controller;
    private final Constants.CLIMBER_POSITIONS extension;
    private int direction = 1;

    public GetToPosition (final Climber climber, final XboxController controller, final Constants.CLIMBER_POSITIONS extension) {

        this.climber = climber;
        this.controller = controller;
        this.extension = extension;

        addRequirements(this.climber);
    }

    @Override
    public void initialize () {

        if (this.climber.getMotorPosition() < this.extension.distance) {

            this.direction = 1;
        } else if (this.climber.getMotorPosition() > this.extension.distance) {

            this.direction = -1;
        }
    }

    @Override
    public void execute () {

        this.climber.setMotor(this.direction * extension.velocity);
    }

    @Override
    public void end (boolean interrupted) {

        this.climber.setMotor(0);
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
        
        Timer.delay(0.1);
        
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
    }

    @Override
    public boolean isFinished () {

        if (this.direction == 1) {

            return this.climber.getMotorPosition() >= extension.distance;
        } else {

            return this.climber.getMotorPosition() <= extension.distance;
        }
    }
}
