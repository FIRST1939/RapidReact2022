package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Limelight;

public class ReadAngle extends InstantCommand {

  private final Limelight limelight;

  private double angle;
  private final DoubleSupplier angleSupplier = () -> angle;

  public ReadAngle(final Limelight limelight) {

    this.limelight = limelight;
  }

  @Override
  public void initialize () {

    this.angle = this.limelight.getHorizontalAngleError();
  }

  public DoubleSupplier getSupplier (){

    System.out.println("Angle: " + angle);
    return this.angleSupplier;
  }
}
