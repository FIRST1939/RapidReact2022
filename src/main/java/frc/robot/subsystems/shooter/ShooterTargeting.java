// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import static frc.robot.Constants.ShooterConstants.SHOOTER_LONG_PIPELINE;
import static frc.robot.Constants.ShooterConstants.SHOOTER_OFF_PIPELINE;

import frc.robot.devices.Limelight;
import frc.robot.devices.Targeting;

/**
 * The shooter is targeted using a limelight to detect the retro-reflective
 * targets at the top of the upper hub.
 */
class ShooterTargeting implements Targeting {
    private final Limelight limelight = new Limelight("limelight-shooter");
    private TargetingType currentType;

    /**
     * Creates a new shooter targeting instance initialized to off.
     */
    public ShooterTargeting() {
        setType(TargetingType.OFF);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation sets the limelight's pipeline and turns the LEDs on or
     * off.
     */
    @Override
    public void setType(TargetingType type) {
        this.currentType = type == null ? TargetingType.OFF : type;
        if (this.currentType == Targeting.TargetingType.OFF) {
            this.limelight.setPipeline(SHOOTER_OFF_PIPELINE);
            this.limelight.setLEDMode(1);
        } else {
            this.limelight.setLEDMode(3);
            this.limelight.setPipeline(SHOOTER_LONG_PIPELINE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetingType getType() {
        return this.currentType == null ? TargetingType.OFF : this.currentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTargetFound() {
        return this.limelight.isTargetFound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHorizontalAngleError() {
        return this.limelight.getHorizontalAngleError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getVerticalAngleError() {
        return this.limelight.getVerticalAngleError();
    }
}
