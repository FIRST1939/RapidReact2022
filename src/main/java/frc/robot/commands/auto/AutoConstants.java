// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

/** Add your docs here. */
public interface AutoConstants {
    /** Drive from edge of tarmac to near cargo. */
    public static final double CLOSE_CARGO_PICKUP_DRIVE_DIST = 48.00;
    /** Drive after near cargo pick up to alignment turn. */
    public static final double CLOSE_CARGO_PICKUP_TO_TURN_DIST = 100.0;
    /** Turn to fender small angle (absolute value). */
    public static final double TURN_TO_FENDER_SMALL_ANGLE = 22.5;
    /** Drive to fender after turn distance. */
    public static final double AFTER_TURN_DRIVE_TO_FENDER_DIST = 50.0;
    /** Drive from fender to exit tarmac. */
    public static final double FROM_FENDER_TO_EXIT_TARMAC_DIST = 150.0;

    public static final double TURN_TO_THIRD_CARGO_ANGLE = 67.5;
}
