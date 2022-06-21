// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.climber;

/**
 * The configurations for known climber hook positions.
 */
public enum ClimberPositions {
    /** Hooks fully retracted (for automation). */
    bottom(8500, 2000),
    /** Hooks fully retracted (for driver). TODO why two of these? */
    bottomFirst(15000, 2000),
    /** Hooks not quite fully retracted on last bar. */
    finalBarRetract(8500, 50000),
    /** Move hooks off current bar in prep for next bar. */
    offBar(10000, 50000),
    /** Partial extension for drive onto first bar from floor. */
    partial(15000, 250000),
    /** Full extension for grabbing next bar from another bar. */
    full(15000, 340000);

    public final int velocity;
    public final int distance;

    private ClimberPositions(int velocity, int distance) {
        this.velocity = velocity;
        this.distance = distance;
    }
}
