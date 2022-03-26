package frc.robot.triggers.lighting;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Intake;

public class IntakeForward extends Trigger {
    
    private final Intake intake;

    public IntakeForward (Intake intake) {

        this.intake = intake;
    }

    @Override
    public boolean get () {

        return this.intake.getIntakeSpeed() > 0;
    }
}
