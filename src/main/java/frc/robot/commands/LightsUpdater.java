// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Lights;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html


public class LightsUpdater extends InstantCommand {
  private final Lights lights;

  private Constants.LEDMode mode;
  public LightsUpdater(final Lights lights, final Constants.LEDMode mode) {
    this.lights = lights;
    this.mode = mode;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    lights.getInstance().setColor(mode);
  }
}
