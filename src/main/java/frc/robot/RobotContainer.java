// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveWithInput;
import frc.robot.commands.indexer.IndexerShootingState;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;
import frc.robot.triggers.ShootTrigger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Create the joystick objects.
  private final Joystick leftStick = new Joystick(Constants.LEFT_STICK_PORT);
  private final Joystick rightStick = new Joystick(Constants.RIGHT_STICK_PORT);
  private final XboxController driver2 = new XboxController(Constants.DRIVER2_CONTROLLER_PORT);
  private final XboxController manual = new XboxController(Constants.MANUAL_CONTROLLER_PORT);

  // The robot's subsystems and commands are defined here...
  private final DriveTrain driveTrain = new DriveTrain();
  private final Indexer indexer = new Indexer(() -> true); //TODO Replace with indexer boolean supplier
  private final Shooter shooter = Shooter.getInstance();
  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure default commands.
    configureDefaultCommands();
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Establishes the default commands for subsystems that need one. A default
   * command is optional.
   */
  private void configureDefaultCommands() {
    this.driveTrain
        .setDefaultCommand(new DriveWithInput(driveTrain,
            () -> enforceDeadband(-leftStick.getY(), Constants.SPEED_DEAD_BAND),
            () -> enforceDeadband(rightStick.getX(), Constants.ROTATE_DEAD_BAND),
            () -> enforceDeadband(leftStick.getX(), Constants.SPEED_DEAD_BAND)));
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    JoystickButton buttonA = new JoystickButton(driver2, XboxController.Button.kA.value);
    ShootTrigger shootTrigger = new ShootTrigger(this.indexer, this.shooter, buttonA);
    shootTrigger.whileActiveContinuous(IndexerShootingState.getInstance(this.indexer));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return null;
  }

  /**
   * @param rawSpeed the raw speed input from the driver.
   * @param deadband the deadband (this amount on either side of 0) being
   *                 enforced.
   * @return
   */
  private double enforceDeadband(double rawSpeed, double deadband) {
    return Math.abs(rawSpeed) < deadband ? 0.0 : rawSpeed;
  }
}
