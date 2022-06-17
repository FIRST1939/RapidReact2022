/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.devices;
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
      table = NetworkTableInstance.getDefault().getTable(name);
    }

    // Set Methods
    // 0 is Vision
    // 1 is Driver Station Camera
    public void setCamMode(double value){
        table.getEntry("camMode").setNumber(value);
    }

    // 0 is Preset Mode in Current Pipeline
    // 1 is Force Off
    // 2 is Force Blink
    // 3 is Force On
    public void setLEDMode(double value){
        table.getEntry("ledMode").setNumber(value);
    }

    // 0 is side-by-side
    // 1 is secondary stream is small
    // 2 is primary stream is small
    public void setStream(double value){
        table.getEntry("stream").setNumber(value);
    }

    // 0-9
    public void setPipeline(double value){
        table.getEntry("pipeline").setNumber(value);
    }


    // Get Methods

    // 0 is no target found
    // 1 is target found
    public boolean isTargetFound(){
        NetworkTableEntry tv = table.getEntry("tv");
        double value = tv.getDouble(0);
        if (value == 1){   
            return true;
        } else {
            return false; 
        }
    }

    // -27 to 27 degrees
    public double getHorizontalAngleError(){
        return table.getEntry("tx").getDouble(0.0);
    }

    // -20.5 to 20.5 degrees
    public double getVerticalAngleError(){
        return table.getEntry("ty").getDouble(0.0);
    }

    // 0% of image to 100% of image
    public double getTargetArea(){
        return table.getEntry("ta").getDouble(0.0);
    }

    public double getTargetDistance(){
        double area = getTargetArea();
        return (DISTANCE_CONSTANT / Math.sqrt(area)); 
    }
    
    public double getTargetExactDistance(){
        double angle = CAM_CALIBRATION - Math.abs(getVerticalAngleError());
        double totalAngle = angle + CAM_ANGLE;
        double totalAngleRadians = Math.toRadians(totalAngle);
        double distance = (CAM_HEIGHT - TARGET_HEIGHT) / Math.tan(totalAngleRadians);
        return distance;
    }

    // In ms
    public double getLatency(){
        return table.getEntry("tl").getDouble(0.0);
    }

    // -90 degrees to 0 degrees
    public double getSkew(){
        return table.getEntry("ts").getDouble(0.0);
    }

    public int getPipeline(){
        return (int) table.getEntry("getpipe").getDouble(0.0);
    }

    public double getCamMode(){
        return table.getEntry("camMode").getDouble(0.0);
    }

    public double getLEDMode(){
        return table.getEntry("ledMode").getDouble(0.0);
    }
}