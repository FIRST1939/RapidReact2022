// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Limelight;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ReadAngle extends InstantCommand {
  private final Limelight limelight;

  private double angle;
  private final DoubleSupplier angleSupplier = () -> angle;

  public ReadAngle(final Limelight limelight) {
    this.limelight = limelight;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    double angle = limelight.getHorizontalAngleError();
  }

  public DoubleSupplier getSupplier(){
    return angleSupplier;
  }
}
