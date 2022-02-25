// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * The indexer consists of one motor to drive cargo movement belts and a beam
 * break sensor to detect that a cargo has arrived at the ready to shoot
 * position in the indexer tower. There is also an encoder that is plugged
 * directly into the Talon SRX motor controller for velocity control.
 */
public class Indexer extends SubsystemBase {
  private final WPI_TalonSRX motor;
  private final DigitalInput beamBreak;
  private final BooleanSupplier priorStageSendingSupplier;

  /**
   * Creates the indexer subsystem. The supplier must indicate if the prior cargo
   * handling stage (probably the intake) is currently sending a cargo toward this
   * indexer.
   * 
   * @param priorStageSendingSupplier the supplier indicating if the prior cargo
   *                                  handling stage is sending a cargo our way.
   */
  public Indexer(final BooleanSupplier priorStageSendingSupplier) {
    this.motor = new WPI_TalonSRX(Constants.INDEXER_MOTOR_CAN_ID);
    this.motor.configFactoryDefault();
    // TODO configure kP and kF for velocity control.
    // Include configuration of attached encoder.
    this.beamBreak = new DigitalInput(Constants.INDEXER_BEAM_BREAK_RECEIVER_DIO);
    this.priorStageSendingSupplier = priorStageSendingSupplier;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for feeding
   * cargo to the shooter.
   */
  public void setToShooterFeedVelocity() {
    this.motor.set(ControlMode.Velocity, Constants.INDEXER_SHOOTER_FEED_VELOCITY);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for receiving
   * cargo from the intake.
   */
  public void setToReceiveVelocity() {
    this.motor.set(ControlMode.Velocity, Constants.INDEXER_RECEIVE_VELOCITY);
  }

  /**
   * Stops the indexer motor and the movement of cargo in the indexer.
   */
  public void stop() {
    this.motor.set(ControlMode.Velocity, 0.0);
  }

  /**
   * @return true if there is a cargo at the ready to shoot sensor.
   */
  public boolean isCargoAtSensor() {
    return !beamBreak.get(); // TODO verify this negation.
  }

  /**
   * @return true if the prior cargo handling stage (probably the intake) is
   *         currently sending a cargo our direction.
   */
  public boolean isPriorStageSending() {
    return this.priorStageSendingSupplier.getAsBoolean();
  }

  /**
   * To be used by manual commands when sensors are compromised.
   * 
   * @param speed the percent output (-1.0 to 1.0) to apply.
   */
  public void setManualSpeed(final double speed) {
    this.motor.set(ControlMode.PercentOutput, speed);
  }
}
