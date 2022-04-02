// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import java.util.ArrayList;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

/**
 * Use this command during autonomous to drive a straight line for a distance
 * passed to the constructor.
 */
public class DriveStraightDistance extends CommandBase {
  
  private final ArrayList<Double> inches;
  private final ArrayList<Double> power;

  private int index = 0;

  // Divide absInches into accel, cruise, and decel sections.
  //private final double[] sectionAbsInches = new double[3];
  private final DriveTrain driveTrain;

  /**
   * @param inches     the inches to drive. The intake end is forward (positive).
   *                   Pass a negative value for backwards.
   * @param driveTrain the drive train being controlled.
   */
  public DriveStraightDistance(final DriveTrain driveTrain, final ArrayList<Double> inches, final ArrayList<Double> power) {

    this.inches = inches;
    this.power = power;

    this.driveTrain = driveTrain;
    addRequirements(this.driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

    this.driveTrain.resetDistance();
    this.driveTrain.resetHeading();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    double turningValue = (-this.driveTrain.getHeading()) * Constants.DRIVE_AUTO_GYRO_STRAIGHT_KP;

    double inches = this.inches.get(index);
    double power = this.power.get(index);

    if (inches < 0) { 
      
      turningValue *= -1; 
      power *= -1;
    }
    
    this.driveTrain.arcadeDrive(
        power,
        turningValue,
        0.0
    );

    if (Math.abs(driveTrain.getDistance()) >= Math.abs(inches)) {

      this.driveTrain.resetDistance();
      this.driveTrain.resetHeading();
      index++;
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {

    return this.index == (((this.inches.size() + this.power.size()) / 2) - 1);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    
    this.driveTrain.stop();
  }

  /*
  private void computeSections() {
    // Start with accel and decel taking 20% each.
    double accelDecelLen = this.absInches * 0.2;
    // Clamp to at least 8 inches and at most 16 inches.
    accelDecelLen = MathUtil.clamp(accelDecelLen, 8.0, 16.0);
    // Make sure a low clamp does not extend the total distance.
    accelDecelLen = accelDecelLen * 2.0 <= this.absInches
        ? accelDecelLen
        : this.absInches / 2.0;
    this.sectionAbsInches[0] = accelDecelLen;
    this.sectionAbsInches[1] = absInches - accelDecelLen - accelDecelLen;
    this.sectionAbsInches[2] = accelDecelLen;
  }*/
}
