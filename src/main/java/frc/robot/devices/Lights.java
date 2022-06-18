// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.devices;

import static frc.robot.Constants.Devices.LIGHTS_PWM;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.Constants;

public class Lights {
  private Spark blinkin = new Spark(LIGHTS_PWM);
  private static Lights lightsInstance = null;

  private Lights() {
  }

  public static final synchronized Lights getInstance() {
    if (lightsInstance == null) {
      lightsInstance = new Lights();
    }
    return lightsInstance;
  }

  /**
   * Sets the on board robot LEDs to the prescribed mode.
   * 
   * @param ledMode the mode to set.
   */
  public void setColor(Constants.LEDMode ledMode) {
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
}
