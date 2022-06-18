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
    /**
     * Constants defining the driver controller connections and general controller
     * behavior.
     */
    public final class Controllers {
        /** Driver 1 left stick port. */
        public static final int LEFT_STICK_PORT = 0;
        /** Driver 1 right stick port. */
        public static final int RIGHT_STICK_PORT = 1;
        /** Driver 2 controller port. */
        public static final int DRIVER2_CONTROLLER_PORT = 2;
        /** Driver 2 trigger axes threshold for button like behavior. */
        public static final double TRIGGER_THRESHOLD = .3;

        /** Dead band for speed axes (normal and sidewinder). */
        public static final double SPEED_DEAD_BAND = 0.1;
        /** Dead band for rotation axis. */
        public static final double ROTATE_DEAD_BAND = 0.1;

        /** Axis dead band used when intaking manually. */
        public static final double MANUAL_INTAKE_DEADBAND = 0.1;

        /** Axis dead band used when indexing manually. */
        public static final double MANUAL_INDEXER_DEADBAND = 0.1;
    }

    /**
     * Constants related to the physical structure and operation of the drive train.
     */
    public final class DriveTrain {
        // Drive train normal drive CAN id constants.
        public static final int LEFT_DRIVE_1_CAN_ID = 11;
        public static final int LEFT_DRIVE_2_CAN_ID = 12;
        public static final int LEFT_DRIVE_3_CAN_ID = 13;
        public static final int RIGHT_DRIVE_1_CAN_ID = 14;
        public static final int RIGHT_DRIVE_2_CAN_ID = 15;
        public static final int RIGHT_DRIVE_3_CAN_ID = 16;

        /** Solenoid channel for sidewinder activation. */
        public static final int SIDEWINDER_PCM_CHANNEL = 3;
        /** Sidewinder motor controller CAN id. */
        public static final int SIDEWINDER_MOTOR_CAN_ID = 30;
        /** Proportional PID gain for straight sidewinding. */
        public static final double SIDEWINDER_kP = 0.03;

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
    }

    /**
     * Constants related to the physical structure and operation of the shooter.
     */
    public final class Shooter {
        /** Shooter hood solenoid channel. */
        public static final int SHOOTER_PCM_CHANNEL = 0;
        /** Shooter motor controller CAN id. */
        public static final int SHOOTER_FLYWHEEL_CAN_ID = 32;

        /** Shooter velocity tolerance in TalonFX sensor units per 100ms. */
        public static final int SHOOTER_VELOCITY_ERROR = 350;

        /** Vertical target angle to shooter velocity linear equation slope. */
        public static final double VISION_M = -1500.0 / 11.0;
        /** Vertical target angle to shooter velocity linear equation y intercept. */
        public static final double VISION_B = 81050.0 / 11.0;
    }

    /**
     * The configurations for known position shots and one to indicate the use of
     * vision and a distance to velocity mapping.
     * TODO move this to separate file in shooter package.
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

    /**
     * Constants related to the physical structure and operation of the intake.
     */
    public final class Intake {
        /** Intake deploy solenoid channel. */
        public static final int INTAKE_PCM_CHANNEL = 2;
        /** Intake motor controller CAN id. */
        public static final int INTAKE_MOTOR_CAN_ID = 7;

        /** The minimum intake velocity in RPM. */
        public static final int INTAKE_MIN_RPM = 600;
        /** The maximum intake velocity in RPM. */
        public static final int INTAKE_MAX_RPM = 2200;
        public static final int INTAKE_ENCODER_CYCLES_PER_ROTATION = 2048;
        /** The ratio of the intake speed to drive speed is this value:1 */
        public static final double INTAKE_SPEED_TO_DRIVE_SPEED_RATIO = 2.0;
        /** Distance the intake wheels surface travels in one revolution. */
        public static final double INTAKE_INCHES_PER_REVOLUTION = 6.28;
        public static final int INTAKE_BEAM_BREAK_RECEIVER_DIO = 0;
        /** Time in seconds to continue intake after cargo detection. */
        public static final double INTAKE_AT_SENSOR_TIME_ADJUSTMENT_SEC = 0.0;
    }

    /**
     * Constants related to the physical structure and operation of the indexer.
     */
    public final class Indexer {
        /** Indexer lead motor controller CAN id. */
        public static final int INDEXER_LEADER_CAN_ID = 10;
        /** Indexer follower motor controller CAN id. */
        public static final int INDEXER_FOLLOWER_CAN_ID = 17;

        public static final int INDEXER_BEAM_BREAK_RECEIVER_DIO = 2;
        public static final double MANUAL_INDEXER_FEED_OUTPUT = -0.8;
        /** Time in seconds to continue indexer run after cargo detection. */
        public static final double INDEXER_AT_SENSOR_TIME_ADJUSTMENT_SEC = 0.0;
    }

    /**
     * Constants related to the physical structure and operation of the climber.
     */
    public final class Climber {
        /** Climber motor controller CAN id. */
        public static final int CLIMBER_MOTOR_CAN_ID = 31;
        /** Channel (forward on double solenoid) to reach for next bar. */
        public static final int CLIMBER_PISTON_FORWARD = 1;
        /** Channel (reverse on double solenoid) to return to vertical. */
        public static final int CLIMBER_PISTON_REVERSE = 7;
    }

    /**
     * The configurations for known climber hook positions.
     * TODO move this to separate file in climber package.
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

    /**
     * Constants related to the physical structure and operation of the simple
     * devices on the robot.
     */
    public final class Devices {
        // Lights constants.
        public static final int LIGHTS_PWM = 2;

        // Vision constants.
        public static final int SHOOTER_LONG_PIPELINE = 0;
        public static final int SHOOTER_OFF_PIPELINE = 1;
    }

    /**
     * LED driver feedback (and bling) colors.
     * TODO move this to separate file in devices package.
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

    /** Prevent misguided creation of a instance of this class. */
    private Constants() {
    }
}
