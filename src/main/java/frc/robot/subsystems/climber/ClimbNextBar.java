package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.CLIMBER_POSITIONS;
import frc.robot.Constants.LEDMode;
import frc.robot.devices.Lights;

public class ClimbNextBar extends SequentialCommandGroup {
    private final Climber climber;

    public ClimbNextBar(final Climber climber) {
        this.climber = climber;

        addCommands(
                new InstantCommand(() -> Lights.getInstance().setColor(LEDMode.FIRE)),
                new GetToPosition(this.climber, CLIMBER_POSITIONS.offBar),
                new SetPiston(this.climber, (Boolean) true),
                new WaitCommand(0.5),
                new GetToPosition(this.climber, CLIMBER_POSITIONS.full));
    }
}
