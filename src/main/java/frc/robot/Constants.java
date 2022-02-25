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

    // Drive train constants.
    public static final int LEFT_DRIVE_1_CAN_ID = 1;
    public static final int LEFT_DRIVE_2_CAN_ID = 2;
    public static final int LEFT_DRIVE_3_CAN_ID = 3;
    public static final int RIGHT_DRIVE_1_CAN_ID = 4;
    public static final int RIGHT_DRIVE_2_CAN_ID = 5;
    public static final int RIGHT_DRIVE_3_CAN_ID = 6;

    public static final int SIDEWINDER_PCM_CHANNEL = 0;
    public static final int SIDEWINDER_MOTOR_CAN_ID = 7;
    /** Sidewinder engages at this left stick x deflection */
    public static final double SIDEWINDER_ENABLE_THRESHOLD = 0.3;
    /** Sidewinder disengages at this left stick x deflection */
    public static final double SIDEWINDER_DISABLE_THRESHOLD = 0.2;
    /** Sidewinder output adjusted toward 0 by this amount while engaged */
    public static final double SIDEWINDER_OUTPUT_OFFSET = 0.1;

    // Shooter constants.
    public static final int SHOOTER_PCM_CHANNEL = 1;
    public static final int SHOOTER_FLYWHEEL_CAN_ID = 8;

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
    public static final int INTAKE_PCM_CHANNEL = 1001;
    public static final int INTAKE_MOTOR_CAN_ID = 1002;

    public static final int INTAKE_BEAM_BREAK_RECEIVER_DIO = 1003;

    public static final double INTAKE_GATHERING_EMPTY_VELOCITY = 1004;
    // Indexer constants
    public static final int INDEXER_MOTOR_CAN_ID = 9;
    public static final int INDEXER_BEAM_BREAK_EMITTER_DIO = 0;
    public static final int INDEXER_BEAM_BREAK_RECEIVER_DIO = 1;
    public static final double INDEXER_SHOOTER_FEED_VELOCITY = 0.5;
    public static final double INDEXER_RECEIVE_VELOCITY = 0.5;
    public static final double MANUAL_INDEXER_DEADBAND = 0.1;
    public static final long INDEXER_AT_SENSOR_TIME_ADJUSTMENT_MS = 0;
}
