// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.lights;

import static frc.robot.Constants.Devices.LIGHTS_PWM;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;

/**
 * The lights subsystem monitors robot status and updates the LED strips on the
 * robot to provide feedback to the drive team.
 */
public class Lights extends SubsystemBase {
  private static Lights lightsInstance = null;

  private Spark blinkin = new Spark(LIGHTS_PWM);
  private DriveTrain driveTrain = null;
  private Intake intake = null;
  private Shooter shooter = null;
  private Feedback currentFeedback = null; // First periodic will set to bling.
  private int currentFeedbackHoldCounter = 0;

  /**
   * Private to only allow one instance to be created.
   */
  private Lights() {
  }

  /**
   * @return the single instance of the subsystem.
   */
  public static final synchronized Lights getInstance() {
    if (lightsInstance == null) {
      lightsInstance = new Lights();
    }
    return lightsInstance;
  }

  /**
   * Called during startup to enable needed subsystems to be available during
   * periodic robot state evaluation.
   * 
   * @param driveTrain the robot's {@link DriveTrain}.
   * @param intake     the robot's {@link Intake}.
   * @param shooter    the robot's {@link Shooter}.
   */
  public void injectSubsystems(final DriveTrain driveTrain, final Intake intake, final Shooter shooter) {
    this.driveTrain = driveTrain;
    this.intake = intake;
    this.shooter = shooter;
  }

  /**
   * Sets the on board robot LEDs to the prescribed mode.
   * 
   * @param ledMode the mode to set.
   */
  public void setColor(LEDMode ledMode) {
    set(ledMode.value);
  }

  /**
   * Blinkin acts a motor controller, each PWM signal corresponds to a certain
   * voltage output.
   * 
   * @param value the PWM output value.
   */
  private void set(double value) {
    if ((value >= -1.0) && (value <= 1.0)) {
      blinkin.set(value);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation uses the {@link Feedback#evaluate()} method to select a
   * feedback and makes it active.
   * 
   * <p>
   * Note that there is one special case to keep {@link Feedback#ROBOT_FULL}
   * active for a minimum amount of time. Without this, the
   * {@link Feedback#TARGETING_IN_PROGRESS} feedback would immediately become
   * active without robot full ever displaying.
   */
  @Override
  public void periodic() {
    final Feedback feedback = Feedback.evaluate();
    this.currentFeedbackHoldCounter--;
    if (feedback != this.currentFeedback) {
      // Just got robot full, make sure it stays for a minimum time.
      if (feedback == Feedback.ROBOT_FULL) {
        this.currentFeedbackHoldCounter = 40; // approx 0.8 sec
      } else if (this.currentFeedback == Feedback.ROBOT_FULL
          && this.currentFeedbackHoldCounter > 0) {
        // Just got something other than robot full
        // and current is robot full, enforce minimum time.
        return;
      }
      // Make the feedback change.
      this.currentFeedback = feedback;
      this.setColor(this.currentFeedback.getLEDMode());
    }
  }

  /**
   * @return the robot's {@link DriveTrain}.
   */
  DriveTrain getDriveTrain() {
    return this.driveTrain;
  }

  /**
   * @return the robot's {@link Intake}.
   */
  Intake getIntake() {
    return this.intake;
  }

  /**
   * @return the robot's {@link Shooter}.
   */
  Shooter getShooter() {
    return this.shooter;
  }
}
