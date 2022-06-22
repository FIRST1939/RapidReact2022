/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.devices;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * This class is a wrapper around the limelight API to provide common
 * retro-reflective tape targeting functions.
 */
public class Limelight {

    private NetworkTable table;

    // TODO figure these out to get area functions working.
    private static final double DISTANCE_CONSTANT = 0.0;
    private static final double CAM_HEIGHT = 0.0;
    private static final double TARGET_HEIGHT = 0.0;
    private static final double CAM_ANGLE = 0.0;
    private static final double CAM_CALIBRATION = 0.0;

    /**
     * Create a new limelight with the given network table name.
     * 
     * @param name the network table name for the limelight.
     */
    public Limelight(String name) {
        table = NetworkTableInstance.getDefault().getTable(name);
    }

    /**
     * <ul>
     * <li>0 is Vision
     * <li>1 is Driver Station Camera
     * </ul>
     * 
     * @param value the camera mode as described above.
     */
    public void setCamMode(double value) {
        table.getEntry("camMode").setNumber(value);
    }

    /**
     * <ul>
     * <li>0 is Preset Mode in Current Pipeline
     * <li>1 is Force Off
     * <li>2 is Force Blink
     * <li>3 is Force On
     * </ul>
     * 
     * @param value the LED mode as described above.
     */
    public void setLEDMode(double value) {
        table.getEntry("ledMode").setNumber(value);
    }

    /**
     * <ul>
     * <li>0 is side-by-side
     * <li>1 is secondary stream is small
     * <li>2 is primary stream is small
     * </ul>
     * 
     * @param value the stream positioning (if a webcam is attached to the
     *              limelight) as described above.
     */
    public void setStream(double value) {
        table.getEntry("stream").setNumber(value);
    }

    /**
     * @param value the limelight iage processing pipeline to use (0-9).
     */
    public void setPipeline(double value) {
        table.getEntry("pipeline").setNumber(value);
    }

    /**
     * @return true if a valid target is in range and false otherwise.
     */
    public boolean isTargetFound() {
        NetworkTableEntry tv = table.getEntry("tv");
        double value = tv.getDouble(0.0);
        return value == 1.0;
    }

    /**
     * @return the horizontal angle to the target (0 if no target). The value is in
     *         the range -27 to 27 degrees where negative is to the left.
     */
    public double getHorizontalAngleError() {
        return table.getEntry("tx").getDouble(0.0);
    }

    /**
     * @return the vertical angle to the target (0 if no target). The value is in
     *         the range -20.5 to 20.5 degrees where negative is down.
     */
    public double getVerticalAngleError() {
        return table.getEntry("ty").getDouble(0.0);
    }

    /**
     * 
     * @return the target area as a percentage of the image area (0 - 100).
     */
    public double getTargetArea() {
        return table.getEntry("ta").getDouble(0.0);
    }

    /**
     * DO NOT USE. DOES NOT WORK, YET.
     */
    public double getTargetDistance() {
        double area = getTargetArea();
        return (DISTANCE_CONSTANT / Math.sqrt(area));
    }

    /**
     * DO NOT USE. DOES NOT WORK, YET.
     */
    public double getTargetExactDistance() {
        double angle = CAM_CALIBRATION - Math.abs(getVerticalAngleError());
        double totalAngle = angle + CAM_ANGLE;
        double totalAngleRadians = Math.toRadians(totalAngle);
        double distance = (CAM_HEIGHT - TARGET_HEIGHT) / Math.tan(totalAngleRadians);
        return distance;
    }

    // In ms
    public double getLatency() {
        return table.getEntry("tl").getDouble(0.0);
    }

    // -90 degrees to 0 degrees
    public double getSkew() {
        return table.getEntry("ts").getDouble(0.0);
    }

    public int getPipeline() {
        return (int) table.getEntry("getpipe").getDouble(0.0);
    }

    public double getCamMode() {
        return table.getEntry("camMode").getDouble(0.0);
    }

    public double getLEDMode() {
        return table.getEntry("ledMode").getDouble(0.0);
    }
}