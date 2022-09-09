package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Lights extends SubsystemBase {
  
  private Spark blinkin = new Spark(Constants.LIGHTS_PWM);
  private static Lights lightsInstance = null;
  
  private Lights () {}

  public static final synchronized Lights getInstance (){

    if (lightsInstance == null){ lightsInstance = new Lights(); }
    return lightsInstance;
  }

  @Override
  public void periodic () {}

  /**
   * Blinkin acts as a motor controller,
   * each PWM signal corresponds to a certain volage output.
   */
  public void set (double value) {

    if ((value >= -1.0) && (value <= 1.0)) {

      this.blinkin.set(value);
    }
  }

  public void setColor (Constants.LEDMode ledMode) {

    this.set(ledMode.value);
  }
}
