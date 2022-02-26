// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.DriveWithInput;

import frc.robot.commands.indexer.IndexerShootingState;
import frc.robot.commands.indexer.ManualIndexer;
import frc.robot.commands.intake.IntakeExtendCommandSelector;
import frc.robot.commands.intake.IntakeRetractCommandSelector;
import frc.robot.commands.intake.ManualIntakeRollerBelts;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.triggers.ShootTrigger;
import frc.robot.commands.climber.Climb;
import frc.robot.commands.climber.ExtendMotor;
import frc.robot.commands.climber.RetractMotor;
import frc.robot.commands.climber.SetPiston;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.DriveTrain;
import frc.robot.triggers.ClimbMotorTrigger;
import frc.robot.triggers.ClimbPistonTrigger;
import frc.robot.triggers.ClimbTrigger;
import frc.robot.triggers.ClimbWinchTrigger;
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
  private final XboxController driverTwo = new XboxController(Constants.DRIVER2_CONTROLLER_PORT);

  // The robot's subsystems and commands are defined here...
  private final DriveTrain driveTrain = new DriveTrain();
  private final Shooter shooter = Shooter.getInstance();
  private final Indexer indexer = new Indexer(() -> true); // TODO indexer boolean supplier
  private final Intake intake = new Intake();
  private final Climber climber = Climber.getInstance();

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

    JoystickButton fenderLowButton = new JoystickButton(driverTwo, XboxController.Button.kY.value);
    fenderLowButton.whenPressed(new SetShot(Shooter.getInstance(), Constants.SHOTS.fenderLow));

    JoystickButton fenderHighButton = new JoystickButton(driverTwo, XboxController.Button.kB.value);
    fenderHighButton.whenPressed(new SetShot(Shooter.getInstance(), Constants.SHOTS.fenderHigh));

    BooleanSupplier shootTriggerSupplier = () -> (driverTwo
        .getRawAxis(XboxController.Axis.kRightTrigger.value) > Constants.TRIGGER_THRESHOLD);
    ShootTrigger shootTrigger = new ShootTrigger(this.indexer, this.shooter, shootTriggerSupplier);
    shootTrigger.whileActiveContinuous(IndexerShootingState.getInstance(this.indexer));

    JoystickButton toggleManualIntakeIndexer = new JoystickButton(driverTwo, XboxController.Button.kStart.value);
    toggleManualIntakeIndexer.toggleWhenActive(new ManualIndexer(this.indexer,
        () -> enforceDeadband(-driverTwo.getRightY(), Constants.MANUAL_INDEXER_DEADBAND)));
    toggleManualIntakeIndexer.toggleWhenActive(new ManualIntakeRollerBelts(this.intake,
        () -> enforceDeadband(-driverTwo.getLeftX(), Constants.MANUAL_INTAKE_DEADBAND)));

    JoystickButton intakeGatherButton = new JoystickButton(driverTwo, XboxController.Button.kRightBumper.value);
    intakeGatherButton.whileHeld(new IntakeExtendCommandSelector(this.intake));
    JoystickButton intakeStopGatherButton = new JoystickButton(driverTwo, XboxController.Button.kLeftBumper.value);
    intakeStopGatherButton.whenPressed(new IntakeRetractCommandSelector(this.intake));

    JoystickButton climberWinchButtonOne = new JoystickButton(driverTwo, XboxController.Button.kLeftStick.value);
    JoystickButton climberWinchButtonTwo = new JoystickButton(driverTwo, XboxController.Button.kRightStick.value);
    ClimbWinchTrigger climbWinchTrigger = new ClimbWinchTrigger(this.climber, climberWinchButtonOne, climberWinchButtonTwo);
    climbWinchTrigger.whileActiveContinuous(new RetractMotor(this.climber));

    BooleanSupplier climberMotorRetract = () -> (driverTwo.getRawAxis(XboxController.Axis.kRightY.value) < -Constants.AXIS_THRESHOLD);
    ClimbMotorTrigger climbMotorRetractTrigger = new ClimbMotorTrigger(this.climber, climberMotorRetract);
    climbMotorRetractTrigger.whileActiveContinuous(new RetractMotor(this.climber));

    BooleanSupplier climberMotorExtend = () -> (driverTwo.getRawAxis(XboxController.Axis.kRightY.value) > Constants.AXIS_THRESHOLD);
    ClimbMotorTrigger climbMotorExtendTrigger = new ClimbMotorTrigger(this.climber, climberMotorExtend);
    climbMotorExtendTrigger.whileActiveContinuous(new ExtendMotor(this.climber));

    BooleanSupplier climberPistonExtend = () -> (driverTwo.getRawAxis(XboxController.Axis.kRightX.value) < -Constants.AXIS_THRESHOLD);
    ClimbPistonTrigger climbPistonExtendTrigger = new ClimbPistonTrigger(this.climber, climberPistonExtend);
    climbPistonExtendTrigger.whileActiveContinuous(new SetPiston(this.climber, true));

    BooleanSupplier climberPistonRetract = () -> (driverTwo.getRawAxis(XboxController.Axis.kRightX.value) > Constants.AXIS_THRESHOLD);
    ClimbPistonTrigger climbPistonRetractTrigger = new ClimbPistonTrigger(this.climber, climberPistonRetract);
    climbPistonRetractTrigger.whileActiveContinuous(new SetPiston(this.climber, false));

    JoystickButton climbButton = new JoystickButton(driverTwo, XboxController.Button.kRightStick.value);
    ClimbTrigger climbTrigger = new ClimbTrigger(this.climber, climbButton);
    climbTrigger.whenActive(new Climb(this.climber));
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
