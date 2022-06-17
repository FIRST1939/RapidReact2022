// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.SHOTS;
import frc.robot.commands.CancelCommand;
import frc.robot.commands.RumbleController;
import frc.robot.commands.ToggleIntakeIndexerManualMode;
import frc.robot.commands.ToggleManualEjection;
import frc.robot.commands.auto.Auto4Ball;
import frc.robot.commands.auto.Auto5Ball;
import frc.robot.commands.auto.CargoRingTwoBall;
import frc.robot.commands.auto.OneBall;
import frc.robot.commands.auto.Rude1Ball;
import frc.robot.commands.auto.Rude2Ball;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.DriveWithInput;
import frc.robot.subsystems.drive.ManualMoveAndTurnToTarget;
import frc.robot.subsystems.drive.ManualTurnToTarget;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.manual.ManualIndexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.manual.IntakeExtendCommandSelector;
import frc.robot.subsystems.intake.manual.IntakeRetractCommandSelector;
import frc.robot.subsystems.intake.manual.ManualIntakeRollerBelts;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.VisionWithDistance;
import frc.robot.subsystems.shooter.triggers.ManualShootTrigger;
import frc.robot.subsystems.shooter.triggers.ShootTrigger;
import frc.robot.subsystems.shooter.triggers.ShooterIdleTrigger;
import frc.robot.commands.climber.ClimbNextBar;
import frc.robot.commands.climber.ClimbToSecond;
import frc.robot.commands.climber.ClimbToThird;
import frc.robot.commands.climber.GetToPosition;
import frc.robot.commands.climber.RetractMotor;
import frc.robot.commands.climber.ExtendMotor;
import frc.robot.commands.climber.SetHome;
import frc.robot.commands.climber.SetPiston;
import frc.robot.devices.Lights;
import frc.robot.devices.Limelight;
import frc.robot.devices.RobotCargoCount;

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
  JoystickButton sidewinderManualDeploy = new JoystickButton(leftStick, 6);
  JoystickButton manualSlowlyDriveButton = new JoystickButton(leftStick, 7);
  private final DriveTrain driveTrain = new DriveTrain(() -> sidewinderManualDeploy.get(), manualSlowlyDriveButton);
  private final RobotCargoCount robotCargoCount = RobotCargoCount.getInstance();
  private final Intake intake = new Intake(() -> this.driveTrain.getRate());
  private final Indexer indexer = new Indexer(() -> this.intake.isSendingCargo());
  private final Shooter shooter = Shooter.getInstance();
  private final Climber climber = Climber.getInstance();
  private final Limelight limelightShooter = new Limelight("limelight-shooter");
  private final RumbleController rumbleController = new RumbleController(this.driverTwo);
  private final SendableChooser<Supplier<Command>> autoChooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure default commands.
    configureDefaultCommands();
    // Configure the button bindings
    configureButtonBindings();
    configureLightingTriggers();
    configureAutoChooser();
  }

  /**
   * Populates the dashboard chooser for auto mode selection.
   */
  private void configureAutoChooser() {
    this.autoChooser.setDefaultOption("One Ball",
        () -> new OneBall(shooter, indexer, driveTrain, limelightShooter));
    this.autoChooser.addOption("Do Nothing", () -> new WaitCommand(1.0));
    this.autoChooser.addOption("Cargo Ring Two Ball",
        () -> new CargoRingTwoBall(driveTrain, intake, indexer, shooter, limelightShooter));
    this.autoChooser.addOption("4 Ball",
        () -> new Auto4Ball(driveTrain, intake, indexer, shooter, limelightShooter));
    this.autoChooser.addOption("Rude 2 Ball",
        () -> new Rude2Ball(driveTrain, intake, indexer, shooter, limelightShooter));
    this.autoChooser.addOption("Rude 1 Ball",
        () -> new Rude1Ball(driveTrain, intake, indexer, shooter, limelightShooter));
    this.autoChooser.addOption("5 Ball",
        () -> new Auto5Ball(driveTrain, intake, indexer, shooter, limelightShooter));

    SmartDashboard.putData("Autonomous Chooser", this.autoChooser);
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
    JoystickButton manualTurnToTargetLong = new JoystickButton(rightStick, 10);
    manualTurnToTargetLong.whenPressed(
        rumbleAfter(new ManualTurnToTarget(driveTrain, limelightShooter, 0)));

    JoystickButton manualMoveToTargetLong = new JoystickButton(rightStick, 11);
    manualMoveToTargetLong.whenPressed(
        rumbleAfter(new ManualMoveAndTurnToTarget(driveTrain, limelightShooter, 0)));

    // shooter buttons
    JoystickButton fenderLowButton = new JoystickButton(driverTwo, XboxController.Button.kY.value);
    fenderLowButton.whenPressed(new SetShot(this.shooter, SHOTS.fenderLow));

    JoystickButton fenderHighButton = new JoystickButton(driverTwo, XboxController.Button.kB.value);
    fenderHighButton.whenPressed(new SetShot(this.shooter, SHOTS.fenderHigh));

    JoystickButton fenderPlusOneLow = new JoystickButton(driverTwo, XboxController.Button.kA.value);
    fenderPlusOneLow.whenPressed(new SetShot(this.shooter, SHOTS.fenderPlusOneLow));

    JoystickButton fenderPlusOneHigh = new JoystickButton(driverTwo, XboxController.Button.kX.value);
    fenderPlusOneHigh.whenPressed(new SetShot(this.shooter, SHOTS.fenderPlusOneHigh));

    JoystickButton visionTracked = new JoystickButton(driverTwo, XboxController.Button.kRightStick.value);
    visionTracked.whenPressed(
        new SetShot(this.shooter, SHOTS.visionTracked).andThen(new VisionWithDistance(shooter, limelightShooter)));

    POVButton cargoRing = new POVButton(driverTwo, 0); // 0 is up, 90 is right, 180 is down, and 270 is left
    cargoRing.whenPressed(new SetShot(this.shooter, SHOTS.cargoRing));

    POVButton launchpad = new POVButton(driverTwo, 90);
    launchpad.whenPressed(new SetShot(this.shooter, SHOTS.launchpad));

    // A button to read a velocity from the dashboard and apply it to the shooter.
    POVButton setVelocity = new POVButton(driverTwo, 180);
    setVelocity.whenPressed(new InstantCommand(
        () -> this.shooter.cargoShot((int) SmartDashboard.getNumber("Shooter Velocity", 6750), true)));

    POVButton offButton = new POVButton(driverTwo, 270);
    offButton.whenPressed(new SetShot(this.shooter, SHOTS.off));

    // A button that will idle the shooter while held.
    ShooterIdleTrigger shooterIdleTrigger = new ShooterIdleTrigger(this.robotCargoCount);
    shooterIdleTrigger.whenActive(new WaitCommand(1.0).andThen(new InstantCommand(shooter::idle)));
    shooterIdleTrigger.whenInactive(new InstantCommand(this.shooter::cargoShot));

    BooleanSupplier shootTriggerSupplier = () -> (driverTwo
        .getRawAxis(Math.abs(XboxController.Axis.kRightTrigger.value)) > Constants.TRIGGER_THRESHOLD);
    ShootTrigger shootTrigger = new ShootTrigger(this.indexer, this.shooter, shootTriggerSupplier);
    shootTrigger.whenActive(new InstantCommand(() -> this.indexer.requestShot()));

    // intake and indexer buttons
    JoystickButton toggleManualIntakeIndexer = new JoystickButton(driverTwo, XboxController.Button.kStart.value);
    toggleManualIntakeIndexer.whenPressed(
        new ToggleIntakeIndexerManualMode(
            this.intake,
            new ManualIntakeRollerBelts(this.intake,
                () -> enforceDeadband(-driverTwo.getLeftX(), Constants.MANUAL_INTAKE_DEADBAND)),
            new ManualIndexer(this.indexer,
                () -> enforceDeadband(driverTwo.getRightY(), Constants.MANUAL_INDEXER_DEADBAND),
                new ManualShootTrigger(indexer, shooter, shootTriggerSupplier))));

    /*
     * This is to be used to eject all cargo from the robot in the case of jams or a
     * wrong color cargo that we want to get rid of immediately. Note that the
     * button must not be released until all cargo is removed to ensure that the
     * automation is reactivated correctly. If this is not possible, release the
     * button and immediately press the button to enter manual mode. Use manual mode
     * to empty the cargo (shooting as appropriate), and then go back to automation.
     */
    JoystickButton manualEjectionIntakeIndexer = new JoystickButton(driverTwo, XboxController.Button.kLeftBumper.value);
    manualEjectionIntakeIndexer.whenPressed(new ToggleManualEjection(intake, indexer));
    manualEjectionIntakeIndexer.whenReleased(new ToggleManualEjection(intake, indexer));

    JoystickButton intakeGatherButton = new JoystickButton(driverTwo, XboxController.Button.kRightBumper.value);
    intakeGatherButton.whenPressed(new IntakeExtendCommandSelector(this.intake));
    intakeGatherButton.whenReleased(new IntakeRetractCommandSelector(this.intake));

    // climber buttons
    JoystickButton climberMotorRetract = new JoystickButton(rightStick, 2);
    climberMotorRetract.whileHeld(new RetractMotor(this.climber));

    JoystickButton climberMotorExtend = new JoystickButton(rightStick, 3);
    climberMotorExtend.whileHeld(new ExtendMotor(this.climber));

    JoystickButton climberPistonRetract = new JoystickButton(rightStick, 4);
    climberPistonRetract.whenPressed(new SetPiston(this.climber, (Boolean) false));

    JoystickButton climberPistonExtend = new JoystickButton(rightStick, 5);
    climberPistonExtend.whenPressed(new SetPiston(this.climber, (Boolean) true));

    Command climbToSecond = rumbleAfter(new ClimbToSecond(this.climber));
    Command climbToThird = rumbleAfter(new ClimbToThird(this.climber));

    JoystickButton climbToNextBarButton = new JoystickButton(leftStick, 4);
    climbToNextBarButton.whenPressed(
        rumbleAfter(new ClimbNextBar(this.climber)));

    JoystickButton climbToSecondButton = new JoystickButton(leftStick, 2);
    climbToSecondButton.whenPressed(climbToSecond);

    JoystickButton climbToThirdButton = new JoystickButton(leftStick, 3);
    climbToThirdButton.whenPressed(climbToThird);

    JoystickButton cancelClimbButton = new JoystickButton(leftStick, 5);
    cancelClimbButton.whenPressed(new CancelCommand(climbToSecond, climbToThird));

    JoystickButton climberMotorPartialPositionExtend = new JoystickButton(rightStick, 6);
    climberMotorPartialPositionExtend.whenPressed(
        rumbleAfter(new GetToPosition(this.climber, Constants.CLIMBER_POSITIONS.partial)));

    JoystickButton climberMotorFullPositionButton = new JoystickButton(rightStick, 9);
    climberMotorFullPositionButton.whenPressed(
        rumbleAfter(new GetToPosition(this.climber, Constants.CLIMBER_POSITIONS.full)));

    JoystickButton climberSetHomeButton = new JoystickButton(driverTwo, XboxController.Button.kBack.value);
    climberSetHomeButton.whenPressed(new SetHome(this.climber));

    JoystickButton climberSetHomeButtonSecondary = new JoystickButton(rightStick, 8);
    climberSetHomeButtonSecondary.whenPressed(new SetHome(this.climber));

    JoystickButton climberMotorBottomPositionButton = new JoystickButton(rightStick, 7);
    climberMotorBottomPositionButton.whenPressed(
        rumbleAfter(new GetToPosition(this.climber, Constants.CLIMBER_POSITIONS.bottomFirst)));
  }

  private void configureLightingTriggers() {
    new Trigger(() -> this.intake.getIntakeSpeed() > 0)
        .whenActive(() -> Lights.getInstance().setColor(Constants.LEDMode.GREEN));
    new Trigger(() -> this.intake.getIntakeSpeed() < 0)
        .whenActive(() -> Lights.getInstance().setColor(Constants.LEDMode.RED));
  }

  /**
   * Initializes contained subsystems for autonomous.
   */
  public void autoInitialization() {
    this.intake.enterAuto();
    this.indexer.enterAuto();
    RobotCargoCount.getInstance().setCount(RobotCargoCount.START_CARGO);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return this.autoChooser.getSelected().get();
  }

  /**
   * Notifies selected subsystems to prepare for auto to teleop transition.
   */
  public void autoExit() {
    this.intake.exitAuto();
    this.indexer.exitAuto();
  }

  public void teleopInitialization() {
    this.intake.enterTeleop();
    this.indexer.enterTeleop();
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

  /**
   * @param enabled turns on vision based target tracking if true. Turns it off,
   *                if false.
   */
  public void enableTargetTracking(boolean enabled) {
    if (enabled) {
      limelightShooter.setPipeline(Constants.SHOOTER_LONG_PIPELINE);
    } else {
      limelightShooter.setPipeline(Constants.SHOOTER_OFF_PIPELINE);
    }
  }

  /**
   * It is desirable to have a single rumble command instance so that if a command
   * tries to rumble while we are already rumbling (schedule the rumble command
   * while it is already running), the second rumble is ignored. The proper way to
   * compose commands in WPILIB (any given commmand and a rumble in this case) is
   * to fold them into a command group, usually via one of the decorator methods
   * on the {@link Command} class (for example,
   * {@link Command#andThen(Command...)}). The problem with this approach in our
   * case is that a single command instance can only be included in one command
   * group. Using andThen directly would require a new rumble commmand instance
   * for each group. That is where this method and its usage of
   * {@link ScheduleCommand} comes in. The schedule command is an instant command
   * that simply schedules the commands it is given. The schedule command will
   * belong to the command group (therefore a new instance each time) but its
   * forked commands, in this case just our single rumble command, will not.
   * 
   * @param command the command after which we wish to rumble.
   * @return a new sequential command group that runs the command and then
   *         rumbles.
   */
  private SequentialCommandGroup rumbleAfter(final Command command) {
    return command.andThen(new ScheduleCommand(this.rumbleController));
  }
}
