// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import static frc.robot.Constants.IndexerConstants.INDEXER_BEAM_BREAK_RECEIVER_DIO;
import static frc.robot.Constants.IndexerConstants.INDEXER_FOLLOWER_CAN_ID;
import static frc.robot.Constants.IndexerConstants.INDEXER_LEADER_CAN_ID;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.devices.RobotCargoCount;
import frc.robot.subsystems.indexer.IndexerStateMachine.State;

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
  private final AtomicReference<FireRequest> firing = new AtomicReference<>(FireRequest.SAFE);
  private final IndexerStateMachine stateMachine;
  private State autoExitState = null;

  /**
   * This enum describes the relationship between the shot triggers, the
   * {@link State#READY_TO_SHOOT} state, and the {@link State#SHOOTING} state.
   */
  private enum FireRequest {
    /** A shot can be requested. */
    SAFE,
    /** A shot has been requested. */
    REQUESTED,
    /** A requested shot is being performed. */
    FIRING;
  }

  /**
   * Creates the indexer subsystem. The supplier must indicate if the prior cargo
   * handling stage (probably the intake) is currently sending a cargo toward this
   * indexer.
   * 
   * @param priorStageSendingSupplier the supplier indicating if the prior cargo
   *                                  handling stage is sending a cargo our way.
   */
  public Indexer(final BooleanSupplier priorStageSendingSupplier) {
    this.leader = new CANSparkMax(INDEXER_LEADER_CAN_ID, MotorType.kBrushless);
    this.leader.restoreFactoryDefaults();
    this.leader.setIdleMode(IdleMode.kBrake);
    this.pidController = this.leader.getPIDController();
    this.pidController.setP(0.1);
    this.pidController.setFF(0.1);

    this.follower = new CANSparkMax(INDEXER_FOLLOWER_CAN_ID, MotorType.kBrushless);
    this.follower.restoreFactoryDefaults();
    this.follower.setIdleMode(IdleMode.kBrake);
    this.follower.follow(this.leader, true);

    this.beamBreak = new DigitalInput(INDEXER_BEAM_BREAK_RECEIVER_DIO);
    this.priorStageSendingSupplier = priorStageSendingSupplier;

    this.stateMachine = new IndexerStateMachine(this);
    setDefaultCommand(this.stateMachine.getDefaultCommand());
    new Trigger(this::isStateMachineRunning)
        .whenInactive(new InstantCommand(() -> firing.set(FireRequest.SAFE)));
  }

  /**
   * We use periodic to update indexer status values on the dashboard.
   * 
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putBoolean("Indexer: ", !this.beamBreak.get());
    String stateName = "<null>";
    final State currentState = this.stateMachine.getCurrentState();
    if (currentState != null) {
      stateName = currentState.name();
    } else {
      final Command current = getCurrentCommand();
      if (current != null) {
        stateName = current.getName();
      }
    }
    SmartDashboard.putString("Indexer State: ", stateName);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for feeding
   * cargo to the shooter.
   */
  public void setToShooterFeedVelocity() {
    // TODO go back to velocity control or remove PID controller.
    // this.pidController.setReference(Constants.INDEXER_SHOOTER_FEED_VELOCITY,
    // ControlType.kVelocity);
    this.leader.set(-0.65);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for receiving
   * cargo from the intake.
   */
  public void setToReceiveVelocity() {
    // TODO go back to velocity control or remove PID controller.
    this.leader.set(-0.3);
  }

  /**
   * Stops the indexer motor and the movement of cargo in the indexer.
   */
  public void stop() {
    this.leader.stopMotor();
  }

  /**
   * @return true if there is a cargo at the ready to shoot sensor.
   */
  public boolean isCargoAtSensor() {
    return !beamBreak.get();
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
    this.leader.set(speed);
  }

  /**
   * @return true when the indexer is ready to shoot.
   */
  public boolean isReadyToShoot() {
    return this.stateMachine.getCurrentState() == State.READY_TO_SHOOT;
  }

  /**
   * @return true if a shot is successfully requested (shot not already requested
   *         nor currently firing).
   */
  public boolean requestShot() {
    return this.firing.compareAndSet(FireRequest.SAFE, FireRequest.REQUESTED);
  }

  /**
   * @return true if a shot request is pending. If so, we move to firing.
   */
  boolean fireShot() {
    return this.firing.compareAndSet(FireRequest.REQUESTED, FireRequest.FIRING);
  }

  /**
   * Used to reset to safe (ready for another shot request) after firing command
   * completes.
   */
  void shotFired() {
    this.firing.set(FireRequest.SAFE);
  }

  /**
   * Sets the proper state for autonomous. Should only be called from autonomous
   * initialization.
   */
  public void enterAuto() {
    this.stateMachine.setNextInitialState(State.READY_TO_SHOOT);
  }

  /**
   * Saves the last state in autonomous for start of teleop. Should only be called
   * from autonomous exit.
   */
  public void exitAuto() {
    this.autoExitState = this.stateMachine.getCurrentState();
  }

  /**
   * Sets the proper state for teleop. Should only be called from teleop
   * initialization.
   */
  public void enterTeleop() {
    if (this.autoExitState == null) {
      this.stateMachine.setNextInitialState(State.EMPTY);
      RobotCargoCount.getInstance().setCount(0);
    } else {
      this.stateMachine.setNextInitialState(this.autoExitState);
      this.autoExitState = null;
    }
  }

  /**
   * @return true if the state machine is running and false otherwise.
   */
  public boolean isStateMachineRunning() {
    return this.stateMachine.getDefaultCommand().isScheduled();
  }
}
