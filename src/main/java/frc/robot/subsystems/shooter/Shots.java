// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

/**
 * The configurations for known position shots and one to indicate the use of
 * vision and a distance to velocity mapping.
 */
public enum Shots {
    /** Turn the shooter off. */
    off(0, false),
    /** Use vision to determine distance and proper velocity. */
    visionTracked(-1, true),
    /** A speed to save power when not shooting. */
    idle(5750, false),
    /** Tuned shot from fender to low goal. */
    fenderLow(3700, true),
    /** Tuned shot from fender to high goal. */
    fenderHigh(5300, false),
    /** Tuned shot to low goal with one robot between us and the fender. */
    fenderPlusOneLow(4200, true),
    /** Tuned shot to high goal with one robot between us and the fender. */
    fenderPlusOneHigh(6400, true),
    /** Tuned shot to high goal from ring of start of match staged cargo. */
    cargoRing(6750, true),
    /** Tuned shot to high goal from mid-field wall. */
    wallShot(7600, true),
    /** Tuned shot to high goal from the closer launchpad. */
    launchpad(8050, true);

    public final int velocity;
    public final boolean hood;

    private Shots(int velocity, boolean hood) {
        this.velocity = velocity;
        this.hood = hood;
    }
}
