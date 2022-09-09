package frc.robot.commands.auto;

public interface AutoConstants {
    /** Drive from edge of tarmac to near cargo. */
    public static final double CLOSE_CARGO_PICKUP_DRIVE_DIST = 35.00;
    /** Drive after near cargo pick up to alignment turn. */
    public static final double CLOSE_CARGO_PICKUP_TO_TURN_DIST = 65.0;
    /** Turn to fender small angle (absolute value). */
    public static final double TURN_TO_FENDER_SMALL_ANGLE = 22.5;
    /** Drive to fender after turn distance. */
    public static final double AFTER_TURN_DRIVE_TO_FENDER_DIST = 30.0;
    /** Drive from fender to exit tarmac. */
    public static final double FROM_FENDER_TO_EXIT_TARMAC_DIST = 100.0;

    public static final double TURN_TO_THIRD_CARGO_ANGLE = 67.5;
}
