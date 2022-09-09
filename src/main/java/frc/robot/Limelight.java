/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

public class Limelight {

    private NetworkTable table;

    private double DISTANCE_CONSTANT = 0.0;
    public static final double CAM_HEIGHT = 0.0;
    public static final double TARGET_HEIGHT = 0.0;
    public static final double CAM_ANGLE = 0.0;
    public static final double CAM_CALIBRATION = 0.0;
    
    public Limelight(String name){
        
      this.table = NetworkTableInstance.getDefault().getTable(name);
    }

    /**
     * @param value
     * 0 is Vision &
     * 1 is Driver Station Camera
    */
    public void setCamMode (double value) {

        this.table.getEntry("camMode").setNumber(value);
    }

    /**
     * @param value
     * 0 is Preset Mode in Current Pipeline &
     * 1 is Force Off &
     * 2 is Force Blink &
     * 3 is Force On
     */
    public void setLEDMode (double value) {

        this.table.getEntry("ledMode").setNumber(value);
    }

    /**
     * @param value
     * 0 is Side-by-Side &
     * 1 is Second Stream is Small &
     * 2 is Primary Stream is Small
     */
    public void setStream (double value) {

        this.table.getEntry("stream").setNumber(value);
    }

    /**
     * @param value
     * 0-9
     */
    public void setPipeline (double value) {

        this.table.getEntry("pipeline").setNumber(value);
    }

    public boolean isTargetFound () {

        NetworkTableEntry tv = this.table.getEntry("tv");
        double value = tv.getDouble(0);
        return value == 1;
    }

    /** 
     * @return
     * -27 to 27 Degrees
    */
    public double getHorizontalAngleError () {

        return this.table.getEntry("tx").getDouble(0.0);
    }

    /**
     * @return
     * -20.5 to 20.5 Degrees
    */
    public double getVerticalAngleError () {

        return this.table.getEntry("ty").getDouble(0.0);
    }

    /**
     * @return
     * 0% to 100% of Image
    */
    public double getTargetArea () {

        return this.table.getEntry("ta").getDouble(0.0);
    }

    public double getTargetDistance () {

        double area = this.getTargetArea();
        return (this.DISTANCE_CONSTANT / Math.sqrt(area)); 
    }
    
    public double getTargetExactDistance () {

        double angle = CAM_CALIBRATION - Math.abs(getVerticalAngleError());
        double totalAngle = angle + CAM_ANGLE;
        double totalAngleRadians = Math.toRadians(totalAngle);
        double distance = (CAM_HEIGHT - TARGET_HEIGHT) / Math.tan(totalAngleRadians);
        return distance;
    }

    /**
     * @return
     * Latency in Milliseconds
    */
    public double getLatency () {

        return this.table.getEntry("tl").getDouble(0.0);
    }

    /**
     * @return
     * -90 to 0 Degrees
     */
    public double getSkew () {

        return this.table.getEntry("ts").getDouble(0.0);
    }

    public int getPipeline () {

        return (int) this.table.getEntry("getpipe").getDouble(0.0);
    }

    public double getCamMode () {

        return this.table.getEntry("camMode").getDouble(0.0);
    }

    public double getLEDMode () {
        
        return this.table.getEntry("ledMode").getDouble(0.0);
    }
}
