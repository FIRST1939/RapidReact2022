// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {
    // Joystick constants.
    public static final int LEFT_STICK_PORT = 0;
    public static final int RIGHT_STICK_PORT = 1;
    public static final double SPEED_DEAD_BAND = 0.1;
    public static final double ROTATE_DEAD_BAND = 0.1;

    public static final int DRIVER2_CONTROLLER_PORT = 2;
    public static final int MANUAL_CONTROLLER_PORT = 3;

    public static final double TRIGGER_THRESHOLD = .3;
    public static final double AXIS_THRESHOLD = .1;

    // Drive train constants.
    public static final int LEFT_DRIVE_1_CAN_ID = 11;
    public static final int LEFT_DRIVE_2_CAN_ID = 12;
    public static final int LEFT_DRIVE_3_CAN_ID = 13;
    public static final int RIGHT_DRIVE_1_CAN_ID = 14;
    public static final int RIGHT_DRIVE_2_CAN_ID = 15;
    public static final int RIGHT_DRIVE_3_CAN_ID = 16;

    public static final int SIDEWINDER_PCM_CHANNEL = 0;
    public static final int SIDEWINDER_MOTOR_CAN_ID = 30;

    /** Sidewinder engages at this left stick x deflection */
    public static final double SIDEWINDER_ENABLE_THRESHOLD = 0.3;
    /** Sidewinder disengages at this left stick x deflection */
    public static final double SIDEWINDER_DISABLE_THRESHOLD = 0.2;
    /** Sidewinder output adjusted toward 0 by this amount while engaged */
    public static final double SIDEWINDER_OUTPUT_OFFSET = 0.1;

    // Drive train encoder DIO ports
    public static final int LEFT_DRIVE_A_CHANNEL = 6;
    public static final int LEFT_DRIVE_B_CHANNEL = 7;
    public static final int RIGHT_DRIVE_A_CHANNEL = 8;
    public static final int RIGHT_DRIVE_B_CHANNEL = 9;
    /** Circumfrence divided by Grayhill pulses per revolution. */
    public static final double DRIVE_INCHES_PER_PULSE = 18.85 /256.0;

    // Shooter constants.
    public static final int SHOOTER_PCM_CHANNEL = 1;
    public static final int SHOOTER_FLYWHEEL_CAN_ID = 32;

    public static final int SHOOTER_DEFAULT_VELOCITY = 0;
    public static final boolean SHOOTER_DEFAULT_HOOD = false; 

    // TODO shooting values
    public enum SHOTS {
        idle(0, false),
        fenderLow(2000, true),
        fenderHigh(4000, false);

        public final int velocity;
        public final boolean hood;

        private SHOTS (int velocity, boolean hood) {

            this.velocity = velocity;
            this.hood = hood;
        }
    }

    public static final int SHOOTER_VELOCITY_ERROR = 10;

    //Intake constants
    public static final int INTAKE_PCM_CHANNEL = 2;
    public static final int INTAKE_MOTOR_CAN_ID = 24;
    /** The minimum intake velocity in Talon SRX native units. */
    public static final int INTAKE_MIN_CLICKS_PER_100MS = 1000;
    /** The maximum intake velocity in Talon SRX native units. */
    public static final int INTAKE_MAX_CLICKS_PER_100MS = 4000;
    /** Circumfrence divided by encoder pulses per revolution. */
    public static final double INTAKE_INCHES_PER_PULSE = 6.28 /2048.0; // TODO verify encoder info
    public static final int INTAKE_BEAM_BREAK_RECEIVER_DIO = 0;
    public static final long INTAKE_AT_SENSOR_TIME_ADJUSTMENT_MS = 0;
    public static final double MANUAL_INTAKE_DEADBAND = 0.1;

    // Indexer constants
    public static final int INDEXER_LEADER_CAN_ID = 10;
    public static final int INDEXER_FOLLOWER_CAN_ID = 17;

    public static final int INDEXER_BEAM_BREAK_EMITTER_DIO = 1;
    public static final int INDEXER_BEAM_BREAK_RECEIVER_DIO = 2;
    public static final double INDEXER_SHOOTER_FEED_VELOCITY = 100;
    public static final double INDEXER_RECEIVE_VELOCITY = 100;
    public static final double MANUAL_INDEXER_DEADBAND = 0.1;
    public static final double MANUAL_INDEXER_FEED_OUTPUT = 0.5;
    public static final long INDEXER_AT_SENSOR_TIME_ADJUSTMENT_MS = 0;

    // Climber constants.
    public static final int CLIMBER_MOTOR_CAN_ID = 31;
    public static final int CLIMBER_PISTON_FORWARD = 3;
    public static final int CLIMBER_PISTON_REVERSE = 4;
    public static final int CLIMBER_EXTENSION_VELOCITY = 0; // TODO extension velocity
    public static final int CLIMBER_ENCODER_EXTEND_CLICKS = 100; // TODO encoder extend clicks
}
