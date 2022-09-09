package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Lights;

public class LightsUpdater extends InstantCommand {

  private final Lights lights = Lights.getInstance();
  private Constants.LEDMode mode;

  public LightsUpdater (final Constants.LEDMode mode) {

    this.mode = mode;
  }

  @Override
  public void initialize () {
    
    this.lights.setColor(mode);
  }
}
