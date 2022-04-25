// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.indexer.IndexerShootingState2;
import frc.robot.commands.state.StateMachineCommand;

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
    this.pidController.setP(0.1);
    this.pidController.setFF(0.1);

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
    SmartDashboard.putBoolean("Indexer: ", !this.beamBreak.get());
    Command current = getCurrentCommand();
    final String cmdName = current != null ? current.getName() : "<null>";
    SmartDashboard.putString("Indexer State: ", cmdName);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for feeding
   * cargo to the shooter.
   */
  public void setToShooterFeedVelocity() {
    //this.pidController.setReference(Constants.INDEXER_SHOOTER_FEED_VELOCITY, ControlType.kVelocity);
    this.leader.set(-0.65);
  }

  /**
   * Sets the Talon SRX on board PID control to the velocity desired for receiving
   * cargo from the intake.
   */
  public void setToReceiveVelocity() {
    this.leader.set(-0.3);
  }

  public void setToRecieveDownVelocity(){
    this.leader.set(0.3);
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
   * @return true if manual mode commands (vs state machine) are running.
   */
  public boolean isManualMode() {
    return this.manualMode;  
  }
  
  /**
   * @return manualMode true to indicate that manual mode commands (vs state
   *                    machine) are running.
   */
  public void setManualMode(final boolean manualMode) {
    this.manualMode = manualMode;
  }

  public enum State {
    EMPTY,
    RECEIVING,
    AT_SENSOR,
    READY_TO_SHOOT,
    SHOOTING
  }

  private final Command emptyStateCommand = new RunCommand(() -> stop(), this)
      .until(() -> isPriorStageSending());
  private final Command receivingStateCommand = new RunCommand(() -> setToReceiveVelocity(), this)
      .until(() -> isCargoAtSensor());
  private final Command atSensorStateCommand = new RunCommand(() -> setToShooterFeedVelocity(), this)
      .withTimeout(0.0);
  private final Command readyToShootStateCommand = new InstantCommand(() -> Lights.getInstance().setColor(LEDMode.RAINBOW), this)
      .andThen(new RunCommand(() -> stop(), this));
  private final Command shootingStateCommand = IndexerShootingState2.getInstance(this);

  private final Map<State, Command> stateMap = new EnumMap<>(State.class);
  {
    stateMap.put(State.EMPTY, emptyStateCommand);
    stateMap.put(State.RECEIVING, receivingStateCommand);
    stateMap.put(State.AT_SENSOR, atSensorStateCommand);
    stateMap.put(State.READY_TO_SHOOT, readyToShootStateCommand);
    stateMap.put(State.SHOOTING, shootingStateCommand);
  }

  private final StateMachineCommand<State> stateMachine = new StateMachineCommand<>(stateMap, this::getNextState);

  private State getNextState(StateMachineCommand<State> machine) {
    final State currentState = machine.getCurrentState();
    State nextState = null;
    if (!isManualMode()) {
      switch (currentState) {
        case EMPTY:
          nextState = State.RECEIVING;

        case RECEIVING:
          nextState = State.AT_SENSOR;

        case AT_SENSOR:
          nextState = State.READY_TO_SHOOT;

        case READY_TO_SHOOT:
          nextState = State.SHOOTING;

        case SHOOTING:
          nextState = State.EMPTY;

        default:
          break;
      }
    }
    return nextState;
  }
}
