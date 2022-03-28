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

    public static final int SIDEWINDER_PCM_CHANNEL = 7;
    public static final int SIDEWINDER_MOTOR_CAN_ID = 30;
    public static final double SIDEWINDER_kP = 0.02;

    public static final double VISION_TURN_TO_ANGLE_kP = .031;
    public static final double VISION_ANGLE_ERROR = 5;

    /** Sidewinder engages at this left stick x deflection */
    public static final double SIDEWINDER_ENABLE_THRESHOLD = 0.3;
    /** Sidewinder disengages at this left stick x deflection */
    public static final double SIDEWINDER_DISABLE_THRESHOLD = 0.2;
    /** Sidewinder output adjusted toward 0 by this amount while engaged */
    public static final double SIDEWINDER_OUTPUT_OFFSET = 0.1;

    // Drive train encoder DIO ports
    public static final int LEFT_DRIVE_A_CHANNEL = 7;
    public static final int LEFT_DRIVE_B_CHANNEL = 6;
    public static final int RIGHT_DRIVE_A_CHANNEL = 9;
    public static final int RIGHT_DRIVE_B_CHANNEL = 8;
    /** Circumfrence divided by Grayhill pulses per revolution. */
    public static final double DRIVE_INCHES_PER_PULSE = 19.24 / 256.0;
    /** A PID p value for gyro based correction for driving straight. */
    public static final double DRIVE_AUTO_GYRO_STRAIGHT_KP = 0.005;
    public static final double DRIVE_AUTO_STRAIGHT_POWER = 0.4;
    /** The in place turn to angle PID p value. i and d are 0. */
    public static final double DRIVE_AUTO_TURN_TO_ANGLE_KP = 0.031;
    public static final double DRIVE_VISION_kP = 0.08;
    public static final double DRIVE_AUTO_TURN_TO_ANGLE_KF = 0.2;
    public static final double DRIVE_AUTO_TURN_TO_ANGLE_TOL_DEG = 5.0;
    public static final double DRIVE_AUTO_TURN_TO_ANGLE_TURN_RATE_TOL_DEG_PER_SEC = 10.0;

    // Shooter constants.
    public static final int SHOOTER_PCM_CHANNEL = 0;
    public static final int SHOOTER_FLYWHEEL_CAN_ID = 32;

    public static final int SHOOTER_DEFAULT_VELOCITY = 0;
    public static final boolean SHOOTER_DEFAULT_HOOD = false;

    // TODO shooting values
    public enum SHOTS {
        off(0, false),
        idle(5750, false),
        fenderLow(3700, true),
        fenderHigh(5300, false),
        fenderPlusOneLow(4200, true),
        fenderPlusOneHigh(6000, true),
        cargoRing(6700, true),
        wallShot(7600, true),
        launchpad(7600, true);

        public final int velocity;
        public final boolean hood;

        private SHOTS(int velocity, boolean hood) {

            this.velocity = velocity;
            this.hood = hood;
        }
    }

    public static final int SHOOTER_VELOCITY_ERROR = 700;

    // Intake constants
    public static final int INTAKE_PCM_CHANNEL = 8;
    public static final int INTAKE_MOTOR_CAN_ID = 7;
    // TODO validate the min and max velocities.
    /** The minimum intake velocity in RPM. */
    public static final int INTAKE_MIN_RPM = 600;
    /** The maximum intake velocity in RPM. */
    public static final int INTAKE_MAX_RPM = 2200;
    public static final int INTAKE_ENCODER_CYCLES_PER_ROTATION = 2048; // TODO verify encoder info
    /** The ratio of the intake speed to drive speed is this value:1 */
    public static final double INTAKE_SPEED_TO_DRIVE_SPEED_RATIO = 2.0;
    public static final double INTAKE_INCHES_PER_REVOLUTION = 6.28;
    public static final int INTAKE_BEAM_BREAK_RECEIVER_DIO = 1;
    public static final long INTAKE_AT_SENSOR_TIME_ADJUSTMENT_MS = 0;
    public static final double MANUAL_INTAKE_DEADBAND = 0.1;

    // Indexer constants
    public static final int INDEXER_LEADER_CAN_ID = 10;
    public static final int INDEXER_FOLLOWER_CAN_ID = 17;

    public static final int INDEXER_BEAM_BREAK_EMITTER_DIO = 1;
    public static final int INDEXER_BEAM_BREAK_RECEIVER_DIO = 2;
    public static final double INDEXER_SHOOTER_FEED_VELOCITY = -700;
    public static final double INDEXER_RECEIVE_VELOCITY = -700;
    public static final double MANUAL_INDEXER_DEADBAND = 0.1;
    public static final double MANUAL_INDEXER_FEED_OUTPUT = -0.8;
    public static final long INDEXER_AT_SENSOR_TIME_ADJUSTMENT_MS = 0;

    // Climber constants.
    public static final int CLIMBER_MOTOR_CAN_ID = 31;
    public static final int CLIMBER_PISTON_FORWARD = 6;
    public static final int CLIMBER_PISTON_REVERSE = 9;
    public static final int CLIMBER_EXTENSION_VELOCITY = 5000; // TODO climber extension velocity
    public static final int CLIMBER_RETRACTION_VELOCITY = 5000; // TODO climber retraction velocity

    // TODO climber extension values 
    public enum CLIMBER_POSITIONS {
        bottom(6000, 2000),
        finalBarRetract(6000, 50000),
        offBar(10000, 50000),
        partial(15000, 250000),
        full(15000, 340000);

        public final int velocity;
        public final int distance;

        private CLIMBER_POSITIONS (int velocity, int distance) {

            this.velocity = velocity;
            this.distance = distance;
        }
    }

    // Compressor constants.
    public static final int PNEUMATICS_HUB_CAN_ID = 1;
    public static final int PNEUMATICS_HUB_MIN_PRESSURE = 110;
    public static final int PNEUMATICS_HUB_MAX_PRESSURE = 120;

    //Lights constants.
    public static final int LIGHTS_PWM = 0;

    //Pipeline constants.
    public static final int SHOOTER_LONG_PIPELINE = 0;
    public static final int SHOOTER_OFF_PIPELINE = 1;
    public static final int SHOOTER_SHORT_PIPELINE = 2;

    public static final int INTAKE_DEFAULT_PIPELINE = 0;
  
    //all these are mostly in red color
    public enum LEDMode{
        BLUE(0.87), RED(0.61), GREEN(0.73), YELLOW(0.67), PURPLE(0.91), PINK(0.57), WHITE(0.93), CONFETTI(-0.87), FIRE(-0.57),
        TWINKLES(-0.49), COLORWAVES(-0.39), SCANNER(-0.35), CHASE(-0.31), STROBE(-0.11), RAINBOW(-0.97), OFF(0.99);
    
        public final double value;
        private LEDMode(double value){
            this.value = value;
        }
    }
}
