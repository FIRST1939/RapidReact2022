// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.lights;

/**
 * LED driver feedback colors.
 */
public enum LEDMode {
    BLUE(0.87),
    RED(0.61),
    GREEN(0.73),
    YELLOW(0.67),
    PURPLE(0.91),
    PINK(0.57),
    WHITE(0.93),
    CONFETTI(-0.87),
    FIRE(-0.57),
    TWINKLES(-0.49),
    COLORWAVES(-0.39),
    SCANNER(-0.35),
    CHASE(-0.31),
    STROBE(-0.11),
    RAINBOW(-0.97),
    OFF(0.99);

    public final double value;

    private LEDMode(double value) {
        this.value = value;
    }
}
