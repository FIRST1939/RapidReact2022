package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.commands.RumbleController;
import frc.robot.subsystems.climber.Climber;

public class GetToPosition extends CommandBase {
    
    private final Climber climber;
    private final RumbleController rumbleController;
    private final Constants.CLIMBER_POSITIONS extension;
    private int direction = 1;

    /**
     * @param climber          the climber being positioned.
     * @param rumbleController a driver rumble feedback command for end of
     *                         positioning. Can be null to not have feedback.
     * @param extension        the desired climber extension.
     */
    public GetToPosition (final Climber climber, final RumbleController rumbleController, final Constants.CLIMBER_POSITIONS extension) {

        this.climber = climber;
        this.rumbleController = rumbleController;
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
    public void end(boolean interrupted) {

        this.climber.setMotor(0);
        if (this.rumbleController != null) {
            this.rumbleController.schedule();
        }
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
