// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.devices;

import edu.wpi.first.math.MathUtil;

/**
 * A single instance of this class keeps track of the total number of cargo
 * present in the robot. There is API available to get, increment and decrement
 * the count. The value must always be 0, 1, or 2.
 */
public class RobotCargoCount {
    /** The robot can be empty of cargo. */
    public static final int MIN_CARGO = 0;
    /** The rules stipulate a maximum of 2. */
    public static final int MAX_CARGO = 2;
    /** We start the match with 1 at ready to shoot. */
    public static final int START_CARGO = 1;

    private static final RobotCargoCount INSTANCE = new RobotCargoCount();

    private int count = START_CARGO;

    /**
     * Private so that just one instance is created above.
     */
    private RobotCargoCount() {
    }

    /**
     * @return the single instance of RobotCargoCount.
     */
    public static RobotCargoCount getInstance()
    {
        return INSTANCE;
    }

    /**
     * @return 0, 1 or 2 for a total robot cargo count.
     */
    public int get() {
        return this.count;
    }

    /**
     * Enforces the count never going over {@link #MAX_CARGO}.
     * 
     * @return true if incremented. Return false if count was 2 or more and
     *         increment failed.
     */
    public boolean increment() {
        if (this.count < MAX_CARGO) {
            this.count++;
            return true;
        }
        return false;
    }

    /**
     * Enforces the count never going under {@link #MIN_CARGO}.
     * 
     * @return true if decremented. Return false if count was 0 or less and
     *         decrement failed.
     */
    public boolean decrement() {
        if (this.count > MIN_CARGO) {
            this.count--;
            return true;
        }
        return false;
    }

    /**
     * @return true if the robot contains no cargo.
     */
    public boolean isEmpty() {
        return this.count == MIN_CARGO;
    }

    /**
     * @return true if the robot contains the rules stipulated maximum.
     */
    public boolean isFull() {
        return this.count == MAX_CARGO;
    }

    /**
     * DO NOT USE THIS! Only for auto and teleop init use for better testing.
     * @param count the count to set, will be clamped to min and max.
     */
    public void setCount(final int count) {
        this.count = MathUtil.clamp(count, MIN_CARGO , MAX_CARGO);
    }
}
