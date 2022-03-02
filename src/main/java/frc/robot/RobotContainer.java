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

import frc.robot.commands.ToggleIntakeIndexerManualMode;
import frc.robot.commands.indexer.IndexerReadyToShootState;
import frc.robot.commands.indexer.IndexerShootingState;
import frc.robot.commands.indexer.ManualIndexer;
import frc.robot.commands.intake.IntakeExtendCommandSelector;
import frc.robot.commands.intake.IntakeGatheringSendState;
import frc.robot.commands.intake.IntakeRetractCommandSelector;
import frc.robot.commands.intake.IntakeStowedEmptyState;
import frc.robot.commands.intake.IntakeStowedSendState;
import frc.robot.commands.intake.ManualIntakeRollerBelts;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.RobotCargoCount;
import frc.robot.subsystems.Shooter;
import frc.robot.triggers.ShootTrigger;
import frc.robot.triggers.ShooterActivateTrigger;
import frc.robot.triggers.ShooterIdleTrigger;
import frc.robot.commands.climber.Climb;
import frc.robot.commands.climber.ExtendMotor;
import frc.robot.commands.climber.RetractMotor;
import frc.robot.commands.climber.SetPiston;
import frc.robot.subsystems.Climber;
import frc.robot.triggers.ClimbTrigger;
import frc.robot.triggers.ManualShootTrigger;

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
  private final RobotCargoCount robotCargoCount = RobotCargoCount.getInstance();
  private final Intake intake = new Intake();
  private final Indexer indexer = new Indexer(() -> isIntakeSendingCargo(), () -> isIntakeInManualMode());
  private final Shooter shooter = Shooter.getInstance();
  private final Climber climber = Climber.getInstance();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure default commands.
    configureDefaultCommands();
    // Configure the button bindings
    configureButtonBindings();
    // Schedule the initial states of the state machines
    scheduleInitialStates();
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
    fenderLowButton.whenPressed(new SetShot(this.shooter, Constants.SHOTS.fenderLow));

    JoystickButton fenderHighButton = new JoystickButton(driverTwo, XboxController.Button.kB.value);
    fenderHighButton.whenPressed(new SetShot(this.shooter, Constants.SHOTS.fenderHigh));

    ShooterIdleTrigger shooterIdleTrigger = new ShooterIdleTrigger(this.robotCargoCount);
    shooterIdleTrigger.whenActive(new SetShot(this.shooter, Constants.SHOTS.idle));

    ShooterActivateTrigger shooterActivateTrigger = new ShooterActivateTrigger(this.indexer);
    shooterActivateTrigger.whenActive(new SetShot(this.shooter, this.shooter.getShot()));

    BooleanSupplier shootTriggerSupplier = () -> (driverTwo
        .getRawAxis(XboxController.Axis.kRightTrigger.value) > Constants.TRIGGER_THRESHOLD);
    ShootTrigger shootTrigger = new ShootTrigger(this.indexer, this.shooter, shootTriggerSupplier);
    shootTrigger.whileActiveContinuous(IndexerShootingState.getInstance(this.indexer));

    JoystickButton toggleManualIntakeIndexer = new JoystickButton(driverTwo, XboxController.Button.kStart.value);
    toggleManualIntakeIndexer.whenPressed(
        new ToggleIntakeIndexerManualMode(
            this.intake,
            new ManualIntakeRollerBelts(this.intake,
                () -> enforceDeadband(-driverTwo.getLeftX(), Constants.MANUAL_INTAKE_DEADBAND)),
            new ManualIndexer(this.indexer,
                () -> enforceDeadband(-driverTwo.getRightY(), Constants.MANUAL_INDEXER_DEADBAND),
                new ManualShootTrigger(indexer, shooter, shootTriggerSupplier))));

    JoystickButton intakeGatherButton = new JoystickButton(driverTwo, XboxController.Button.kRightBumper.value);
    intakeGatherButton.whileHeld(new IntakeExtendCommandSelector(this.intake));
    JoystickButton intakeStopGatherButton = new JoystickButton(driverTwo, XboxController.Button.kLeftBumper.value);
    intakeStopGatherButton.whenPressed(new IntakeRetractCommandSelector(this.intake));

    JoystickButton climbButton = new JoystickButton(leftStick, 3);
    ClimbTrigger climbTrigger = new ClimbTrigger(this.climber, climbButton);
    climbTrigger.whenActive(new Climb(this.climber));

    JoystickButton climberMotorRetract = new JoystickButton(rightStick, 2);
    climberMotorRetract.whileHeld(new RetractMotor(this.climber));

    JoystickButton climberMotorExtend = new JoystickButton(rightStick, 3);
    climberMotorExtend.whileHeld(new ExtendMotor(this.climber));

    JoystickButton climberPistonRetract = new JoystickButton(rightStick, 4);
    climberPistonRetract.whenPressed(new SetPiston(this.climber, (Boolean) true));

    JoystickButton climberPistonExtend = new JoystickButton(rightStick, 5);
    climberPistonExtend.whenPressed(new SetPiston(this.climber, (Boolean) true));
  }

  /**
   * Schedules the initial state commands for the subsystem state machines.
   */
  private void scheduleInitialStates() {
    IntakeStowedEmptyState.getInstance(this.intake).schedule();
    IndexerReadyToShootState.getInstance(this.indexer).schedule();
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

  private boolean isIntakeSendingCargo() {
    final Command currentIntakeCommand = this.intake.getCurrentCommand();
    return (currentIntakeCommand instanceof IntakeGatheringSendState)
        || (currentIntakeCommand instanceof IntakeStowedSendState);
  }

  private boolean isIntakeInManualMode() {
    return this.intake.isManualMode();
  }
}
