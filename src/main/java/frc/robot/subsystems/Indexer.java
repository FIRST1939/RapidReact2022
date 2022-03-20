// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * The indexer consists of one motor to drive cargo movement belts and a beam
 * break sensor to detect that a cargo has arrived at the ready to shoot
 * position in the indexer tower. There is also an encoder that is plugged
 * directly into the Talon SRX motor controller for velocity control.
 */
public class Indexer extends SubsystemBase {
  private final CANSparkMax leader;
  private final SparkMaxPIDController pidController;
  private final CANSparkMax follower;
  private final DigitalInput beamBreak;
  private final BooleanSupplier priorStageSendingSupplier;
  private boolean manualMode = false;

  /**
   * Creates the indexer subsystem. The supplier must indicate if the prior cargo
   * handling stage (probably the intake) is currently sending a cargo toward this
   * indexer.
   * 
   * @param priorStageSendingSupplier the supplier indicating if the prior cargo
   *                                  handling stage is sending a cargo our way.
   * @param manualModeSupplier        the supplier indicating if the indexer
   *                                  should consider itself in manual mode.
   */
  public Indexer(final BooleanSupplier priorStageSendingSupplier) {
    this.leader = new CANSparkMax(Constants.INDEXER_LEADER_CAN_ID, MotorType.kBrushless);
    this.leader.restoreFactoryDefaults();
    this.leader.setIdleMode(IdleMode.kBrake);
    this.pidController = this.leader.getPIDController();
    // TODO configure kP and kF for velocity control.
    this.pidController.setFF(0.1);
    this.pidController.setP(0.1);

    this.follower = new CANSparkMax(Constants.INDEXER_FOLLOWER_CAN_ID, MotorType.kBrushless);
    this.follower.restoreFactoryDefaults();
    this.follower.setIdleMode(IdleMode.kBrake);
    this.follower.follow(this.leader, true);

    this.beamBreak = new DigitalInput(Constants.INDEXER_BEAM_BREAK_RECEIVER_DIO);
    this.priorStageSendingSupplier = priorStageSendingSupplier;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putBoolean("Indexer BeamBreak: ", !this.beamBreak.get());
    Command current = getCurrentCommand();
    SmartDashboard.putString("Indexer Active State: ", current != null ? current.getName() : "<null>");
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for feeding
   * cargo to the shooter.
   */
  public void setToShooterFeedVelocity() {
    //this.pidController.setReference(Constants.INDEXER_SHOOTER_FEED_VELOCITY, ControlType.kVelocity);
    this.pidController.setReference(-0.3, ControlType.kDutyCycle);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for receiving
   * cargo from the intake.
   */
  public void setToReceiveVelocity() {
    //this.pidController.setReference(Constants.INDEXER_RECEIVE_VELOCITY, ControlType.kVelocity);
    this.pidController.setReference(-0.5, ControlType.kDutyCycle);
  }

  /**
   * Stops the indexer motor and the movement of cargo in the indexer.
   */
  public void stop() {
    this.pidController.setReference(0.0, ControlType.kDutyCycle);
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
    // TODO once we have some experience, consider limiting this power.
    this.leader.set(speed);
  }

  /**
   * @return true if manual mode commands (vs state machine) are running.
   */
  public boolean isManualMode() {
    return this.manualModeSupplier.getAsBoolean();
  }
  
  /**
   * @return manualMode true to indicate that manual mode commands (vs state
   *                    machine) are running.
   */
  public void setManualMode(final boolean manualMode) {
    this.manualMode = manualMode;
  }
}
