// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Constants.LEDMode;
import frc.robot.commands.PostLoopCommandScheduler;
import frc.robot.subsystems.RobotCargoCount;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  @Override
  public void robotInit () {

    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    this.m_robotContainer = new RobotContainer();
    this.m_robotContainer.getLights().setColor(LEDMode.FIRE);

    SmartDashboard.putNumber("Auto Start Wait", 0.0);
    SmartDashboard.putNumber("Shooter Velocity", 6750);

    CameraServer.startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic () {

    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
    PostLoopCommandScheduler.scheduleCommands();
    SmartDashboard.putNumber("Cargo Count", RobotCargoCount.getInstance().get());
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit () {

    this.m_robotContainer.limelightTurret.setPipeline(Constants.SHOOTER_OFF_PIPELINE);
  }

  @Override
  public void disabledPeriodic () {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit () {

    this.m_robotContainer.scheduleInitialStates();
    this.m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    this.m_robotContainer.limelightTurret.setPipeline(Constants.SHOOTER_LONG_PIPELINE);

    if (this.m_autonomousCommand != null) {

      this.m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic () {}

  @Override
  public void autonomousExit () {

    this.m_robotContainer.stashAutoExitStateCommands();
  }

  @Override
  public void teleopInit () {

    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (this.m_autonomousCommand != null) {

      this.m_autonomousCommand.cancel();
    }

    this.m_robotContainer.restartAutoExitStateCommands();
    this.m_robotContainer.limelightTurret.setPipeline(Constants.SHOOTER_LONG_PIPELINE);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic () {}

  @Override
  public void testInit () {

    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic () {}
}
