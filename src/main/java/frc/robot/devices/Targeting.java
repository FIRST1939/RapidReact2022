// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.devices;

/**
 * Implementations of this interface will return information required to
 * position the robot (or part of the robot) relative to an obtained target.
 * 
 * <p>
 * One known implementation is to use a limelight to find retroreflective vision
 * targets. Other vision technology and/or target type implemenations could also
 * be created.
 */
public interface Targeting {
    /**
     * Some games, like Steamworks, have more than one target geometry of the same
     * type (e.g retro-reflective tape) to be detected for different game purposes
     * (the most I have seen is two of one type). This enum provides the ability for
     * a targeting system to enable both geometries. If a game only has one type
     * (like Rapid React) or a sensor type can only detect one geometry, the
     * targeting implementation may map both primary and secondary to the same
     * detection algorithm.
     */
    public enum TargetingType {
        PRIMARY,
        SECONDARY,
        OFF
    }

    /**
     * Targeting interface implementations that are always on may do nothing in
     * their implementation of this method.
     * 
     * @param type the type of targeting to be performed.
     */
    public void setType(final TargetingType type);

    /**
     * @return the current targeting type in use. Never null.
     */
    public TargetingType getType();

    /**
     * @return true if a valid target has been found and false otherwise.
     */
    public boolean isTargetFound();

    /**
     * A target to the right will result in a positive angle (to the left is
     * negative).
     * 
     * @return the horizontal angle to the target. Will be 0.0 if
     *         {@link #isTargetObtained()} return false.
     */
    public double getHorizontalAngleError();

    /**
     * A target above the sensor will result in a positive angle (below is
     * negative).
     * 
     * @return the vertical angle to the target. Will be 0.0 if
     *         {@link #isTargetObtained()} return false.
     */
    public double getVerticalAngleError();
}
