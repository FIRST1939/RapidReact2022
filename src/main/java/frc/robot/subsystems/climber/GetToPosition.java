package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ClimberPositions;

public class GetToPosition extends CommandBase {
    private final Climber climber;
    private final ClimberPositions extension;
    private int direction = 1;

    /**
     * @param climber   the climber being positioned.
     * @param extension the desired climber extension.
     */
    public GetToPosition(final Climber climber, final ClimberPositions extension) {
        this.climber = climber;
        this.extension = extension;
        addRequirements(this.climber);
    }

    @Override
    public void initialize() {
        if (this.climber.getMotorPosition() > this.extension.distance) {
            this.direction = -1;
        } else {
            // Commands can run more than once. Always fully initialize.
            this.direction = 1;
        }
    }

    @Override
    public void execute() {
        this.climber.setMotor(this.direction * extension.velocity);
    }

    @Override
    public void end(boolean interrupted) {
        this.climber.setMotor(0);
    }

    @Override
    public boolean isFinished() {
        if (this.direction == 1) {
            return this.climber.getMotorPosition() >= extension.distance;
        } else {
            return this.climber.getMotorPosition() <= extension.distance;
        }
    }
}
