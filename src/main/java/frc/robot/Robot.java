// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.devices.RobotCargoCount;
import frc.robot.devices.Targeting.TargetingType;
import frc.robot.subsystems.shooter.Shooter;

/**
 * The {@link Main} is coded so that {@link RobotBase} will create a new
 * instance of this class. {@link TimedRobot} (a subclass of RobotBase) is coded
 * to run it as the robot for the match, and to call the functions corresponding
 * to each mode, as described in the TimedRobot documentation.
 * 
 * <p>
 * If you change the name of this class or the package after creating this
 * project, you must also update the {@link Main#main(String...)} method to use
 * the new class name. Note that the build.gradle file is configured to have the
 * VM start the {@link Main} class. There is no need to change the build.gradle
 * file for the purpose of renaming this class, nor should the {@link Main}
 * class ever be renamed.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    // Add shooter tuning fields to the dashboard.
    SmartDashboard.putNumber("Auto Start Wait", 0.0);
    SmartDashboard.putNumber("Shooter Velocity", 6750);

    // Start streaming the intake camera to the dashboard.
    CameraServer.startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled commands, running already-scheduled commands, removing
    // finished or interrupted commands, and running subsystem periodic() methods.
    // This must be called from the robot's periodic block in order for anything in
    // the Command-based framework to work.
    CommandScheduler.getInstance().run();
    SmartDashboard.putNumber("Cargo Count: ", RobotCargoCount.getInstance().get());
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Our implementation turns off shooter vision targeting so as to bling the
   * lights rather than target and not blind the setup crew while positioning the
   * robot.
   */
  @Override
  public void disabledInit() {
    Shooter.getInstance().getTargeting().setType(TargetingType.OFF);
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Overridden simply to get rid of the "Override me!" spam.
   */
  @Override
  public void disabledPeriodic() {
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation starts the automode chosen by the drive team and makes
   * sure that automode can target the hub.
   */
  @Override
  public void autonomousInit() {
    m_robotContainer.autoInitialization();
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    Shooter.getInstance().getTargeting().setType(TargetingType.PRIMARY);

    // Schedule the autonomous command.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Overridden simply to get rid of the "Override me!" spam.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation notifies selected subsystems to prepare for the auto to
   * teleop transition.
   */
  @Override
  public void autonomousExit() {
    this.m_robotContainer.autoExit();
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * This implementation makes sure that the automode command is terminated and
   * notifies selected subsystems to complete the auto to teleop transition.
   */
  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    this.m_robotContainer.teleopInitialization();

    Shooter.getInstance().getTargeting().setType(TargetingType.PRIMARY);
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Overridden simply to get rid of the "Override me!" spam.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Cancels all running commands at the start of test mode.
   */
  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * Overridden simply to get rid of the "Override me!" spam.
   */
  @Override
  public void testPeriodic() {
  }
}
