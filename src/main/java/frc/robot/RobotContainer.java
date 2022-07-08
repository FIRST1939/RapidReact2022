// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static frc.robot.Constants.ControllerConstants.CANCEL_CLIMB;
import static frc.robot.Constants.ControllerConstants.DRIVER2_CONTROLLER_PORT;
import static frc.robot.Constants.ControllerConstants.FENDER_HIGH;
import static frc.robot.Constants.ControllerConstants.FENDER_LOW;
import static frc.robot.Constants.ControllerConstants.FENDER_PLUS_ONE_HIGH;
import static frc.robot.Constants.ControllerConstants.FENDER_PLUS_ONE_LOW;
import static frc.robot.Constants.ControllerConstants.INTAKE_GATHER;
import static frc.robot.Constants.ControllerConstants.LEFT_STICK_PORT;
import static frc.robot.Constants.ControllerConstants.MANUAL_EJECT_CARGO;
import static frc.robot.Constants.ControllerConstants.MANUAL_EXTEND_ARMS;
import static frc.robot.Constants.ControllerConstants.MANUAL_EXTEND_HOOKS;
import static frc.robot.Constants.ControllerConstants.MANUAL_FULL_EXTEND_HOOKS;
import static frc.robot.Constants.ControllerConstants.MANUAL_FULL_RETRACT_HOOKS;
import static frc.robot.Constants.ControllerConstants.MANUAL_INDEXER_DEADBAND;
import static frc.robot.Constants.ControllerConstants.MANUAL_INTAKE_DEADBAND;
import static frc.robot.Constants.ControllerConstants.MANUAL_PARTIAL_EXTEND_HOOKS;
import static frc.robot.Constants.ControllerConstants.MANUAL_RETRACT_ARMS;
import static frc.robot.Constants.ControllerConstants.MANUAL_RETRACT_HOOKS;
import static frc.robot.Constants.ControllerConstants.MANUAL_SET_HOME_SECONDARY;
import static frc.robot.Constants.ControllerConstants.NEXT_BAR;
import static frc.robot.Constants.ControllerConstants.POV_CARGO_RING;
import static frc.robot.Constants.ControllerConstants.POV_LAUNCHPAD;
import static frc.robot.Constants.ControllerConstants.POV_OFF;
import static frc.robot.Constants.ControllerConstants.POV_SHOT_TUNING_FROM_DASHBOARD;
import static frc.robot.Constants.ControllerConstants.RIGHT_STICK_PORT;
import static frc.robot.Constants.ControllerConstants.ROTATE_DEAD_BAND;
import static frc.robot.Constants.ControllerConstants.SECOND_BAR;
import static frc.robot.Constants.ControllerConstants.SET_HOME;
import static frc.robot.Constants.ControllerConstants.SHOOT_TRIGGER;
import static frc.robot.Constants.ControllerConstants.SPEED_DEAD_BAND;
import static frc.robot.Constants.ControllerConstants.THIRD_BAR;
import static frc.robot.Constants.ControllerConstants.TOGGLE_MANUAL_MODE;
import static frc.robot.Constants.ControllerConstants.TRIGGER_THRESHOLD;
import static frc.robot.Constants.ControllerConstants.TURN_TO_TARGET;
import static frc.robot.Constants.ControllerConstants.TURN_TO_TARGET_THEN_MOVE_TO_CARGO_RING;
import static frc.robot.Constants.ControllerConstants.VISION_TRACKED;

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
import frc.robot.commands.auto.Auto4Ball;
import frc.robot.commands.auto.Auto5Ball;
import frc.robot.commands.auto.CargoRingTwoBall;
import frc.robot.commands.auto.OneBall;
import frc.robot.commands.auto.Rude1Ball;
import frc.robot.commands.auto.Rude2Ball;
import frc.robot.commands.multisub.ToggleIntakeIndexerManualMode;
import frc.robot.commands.multisub.ToggleManualEjection;
import frc.robot.commands.util.CancelCommand;
import frc.robot.commands.util.RumbleController;
import frc.robot.devices.RobotCargoCount;
import frc.robot.subsystems.climber.ClimbNextBar;
import frc.robot.subsystems.climber.ClimbToSecond;
import frc.robot.subsystems.climber.ClimbToThird;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.climber.ClimberPositions;
import frc.robot.subsystems.climber.ExtendMotor;
import frc.robot.subsystems.climber.GetToPosition;
import frc.robot.subsystems.climber.RetractMotor;
import frc.robot.subsystems.climber.SetPiston;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.DriveWithInput;
import frc.robot.subsystems.drive.TurnToTarget;
import frc.robot.subsystems.drive.TurnToTargetThenMoveToCargoRing;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.manual.ManualIndexer;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.manual.ManualIntakeRollerBelts;
import frc.robot.subsystems.lights.Lights;
import frc.robot.subsystems.shooter.SetShot;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.Shots;
import frc.robot.subsystems.shooter.VisionWithDistance;
import frc.robot.subsystems.shooter.triggers.ManualShootTrigger;
import frc.robot.subsystems.shooter.triggers.ShootTrigger;
import frc.robot.subsystems.shooter.triggers.ShooterIdleTrigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
        // Create the joystick objects.
        private final Joystick leftStick = new Joystick(LEFT_STICK_PORT);
        private final Joystick rightStick = new Joystick(RIGHT_STICK_PORT);
        private final XboxController driverTwo = new XboxController(DRIVER2_CONTROLLER_PORT);

        // The robot's subsystems are defined here...
        private final JoystickButton sidewinderManualDeploy = new JoystickButton(leftStick, 6);
        private final JoystickButton manualSlowlyDriveButton = new JoystickButton(leftStick, 7);
        private final DriveTrain driveTrain = new DriveTrain(sidewinderManualDeploy::get, manualSlowlyDriveButton);
        private final RobotCargoCount robotCargoCount = RobotCargoCount.getInstance();
        private final Intake intake = new Intake(this.driveTrain::getRate);
        private final Indexer indexer = new Indexer(this.intake::isSendingCargo);
        private final Shooter shooter = Shooter.getInstance();
        private final Climber climber = Climber.getInstance();
        private final Command rumbleController = new RumbleController(this.driverTwo)
                        .raceWith(new WaitCommand(1.0)); // Rumble for 1 second.
        private final SendableChooser<Supplier<Command>> autoChooser = new SendableChooser<>();

        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        public RobotContainer() {
                Lights.getInstance().injectSubsystems(this.driveTrain, this.intake, this.shooter);
                // Configure default commands.
                configureDefaultCommands();
                // Configure the button bindings
                configureButtonBindings();
                configureAutoChooser();
        }

        /**
         * Populates the dashboard chooser for auto mode selection.
         */
        private void configureAutoChooser() {
                this.autoChooser.setDefaultOption("One Ball",
                                () -> new OneBall(driveTrain, indexer, shooter));
                this.autoChooser.addOption("Do Nothing", () -> new WaitCommand(1.0));
                this.autoChooser.addOption("Cargo Ring Two Ball",
                                () -> new CargoRingTwoBall(driveTrain, intake, indexer, shooter));
                this.autoChooser.addOption("4 Ball",
                                () -> new Auto4Ball(driveTrain, intake, indexer, shooter));
                this.autoChooser.addOption("Rude 2 Ball",
                                () -> new Rude2Ball(driveTrain, intake, indexer, shooter));
                this.autoChooser.addOption("Rude 1 Ball",
                                () -> new Rude1Ball(driveTrain, intake, indexer, shooter));
                this.autoChooser.addOption("5 Ball",
                                () -> new Auto5Ball(driveTrain, intake, indexer, shooter));

                SmartDashboard.putData("Autonomous Chooser", this.autoChooser);
        }

        /**
         * Establishes the default commands for subsystems that need one. A default
         * command is optional.
         */
        private void configureDefaultCommands() {
                this.driveTrain
                                .setDefaultCommand(new DriveWithInput(driveTrain,
                                                () -> enforceDeadband(-leftStick.getY(), SPEED_DEAD_BAND),
                                                () -> enforceDeadband(rightStick.getX(), ROTATE_DEAD_BAND),
                                                () -> enforceDeadband(leftStick.getX(), SPEED_DEAD_BAND)));
        }

        /**
         * Use this method to define your button->command mappings. Buttons can be
         * created by instantiating a {@link GenericHID} or one of its subclasses
         * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
         * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
         */
        private void configureButtonBindings() {
                new JoystickButton(rightStick, TURN_TO_TARGET)
                                .whenPressed(rumbleAfter(
                                                new TurnToTarget(driveTrain, shooter.getTargeting())));

                new JoystickButton(rightStick, TURN_TO_TARGET_THEN_MOVE_TO_CARGO_RING)
                                .whenPressed(rumbleAfter(
                                                new TurnToTargetThenMoveToCargoRing(
                                                                driveTrain,
                                                                shooter.getTargeting())));

                // shooter buttons
                new JoystickButton(driverTwo, FENDER_LOW)
                                .whenPressed(new SetShot(this.shooter, Shots.fenderLow));

                new JoystickButton(driverTwo, FENDER_HIGH)
                                .whenPressed(new SetShot(this.shooter, Shots.fenderHigh));

                new JoystickButton(driverTwo, FENDER_PLUS_ONE_LOW)
                                .whenPressed(new SetShot(this.shooter, Shots.fenderPlusOneLow));

                new JoystickButton(driverTwo, FENDER_PLUS_ONE_HIGH)
                                .whenPressed(new SetShot(this.shooter, Shots.fenderPlusOneHigh));

                new JoystickButton(driverTwo, VISION_TRACKED)
                                .whenPressed(new SetShot(this.shooter, Shots.visionTracked)
                                                .andThen(new VisionWithDistance(this.shooter)));

                new POVButton(driverTwo, POV_CARGO_RING)
                                .whenPressed(new SetShot(this.shooter, Shots.cargoRing));

                new POVButton(driverTwo, POV_LAUNCHPAD)
                                .whenPressed(new SetShot(this.shooter, Shots.launchpad));

                // A button to read a velocity from the dashboard and apply it to the shooter.
                new POVButton(driverTwo, POV_SHOT_TUNING_FROM_DASHBOARD)
                                .whenPressed(new InstantCommand(
                                                () -> this.shooter.cargoShot((int) SmartDashboard
                                                                .getNumber("Shooter Velocity", 6750), true)));

                new POVButton(driverTwo, POV_OFF)
                                .whenPressed(new SetShot(this.shooter, Shots.off));

                // The shooter is automatically idled when we have no cargo.
                new ShooterIdleTrigger(this.robotCargoCount)
                                .whenActive(new WaitCommand(1.0).andThen(new InstantCommand(shooter::idle)))
                                .whenInactive(new InstantCommand(this.shooter::cargoShot));

                // Button like axes supplier for regular (here) and manual shooting (below).
                BooleanSupplier shootTriggerSupplier = () -> (driverTwo
                                .getRawAxis(Math.abs(SHOOT_TRIGGER)) > TRIGGER_THRESHOLD);
                new ShootTrigger(this.indexer, this.shooter, shootTriggerSupplier)
                                .whenActive(new InstantCommand(this.indexer::requestShot));

                // intake and indexer buttons
                new JoystickButton(driverTwo, TOGGLE_MANUAL_MODE)
                                .whenPressed(new ToggleIntakeIndexerManualMode(
                                                this.intake,
                                                new ManualIntakeRollerBelts(
                                                                this.intake,
                                                                () -> enforceDeadband(
                                                                                -driverTwo.getLeftX(),
                                                                                MANUAL_INTAKE_DEADBAND)),
                                                new ManualIndexer(
                                                                this.indexer,
                                                                () -> enforceDeadband(
                                                                                driverTwo.getRightY(),
                                                                                MANUAL_INDEXER_DEADBAND),
                                                                new ManualShootTrigger(
                                                                                indexer,
                                                                                shooter,
                                                                                shootTriggerSupplier))));

                /*
                 * This is to be used to eject all cargo from the robot in the case of jams or a
                 * wrong color cargo that we want to get rid of immediately. Note that the
                 * button must not be released until all cargo is removed to ensure that the
                 * automation is reactivated correctly. If this is not possible, release the
                 * button and immediately press the button to enter manual mode. Use manual mode
                 * to empty the cargo (shooting as appropriate), and then go back to automation.
                 */
                new JoystickButton(driverTwo, MANUAL_EJECT_CARGO)
                                .whenPressed(new ToggleManualEjection(intake, indexer))
                                .whenReleased(new ToggleManualEjection(intake, indexer));

                new JoystickButton(driverTwo, INTAKE_GATHER)
                                .whenPressed(new InstantCommand(intake::requestExtension))
                                .whenReleased(new InstantCommand(intake::requestRetraction));

                // climber buttons
                new JoystickButton(rightStick, MANUAL_RETRACT_HOOKS)
                                .whileHeld(new RetractMotor(this.climber));

                new JoystickButton(rightStick, MANUAL_EXTEND_HOOKS)
                                .whileHeld(new ExtendMotor(this.climber));

                new JoystickButton(rightStick, MANUAL_RETRACT_ARMS)
                                .whenPressed(new SetPiston(this.climber, (Boolean) false));

                new JoystickButton(rightStick, MANUAL_EXTEND_ARMS)
                                .whenPressed(new SetPiston(this.climber, (Boolean) true));

                final Command climbToNextBar = rumbleAfter(new ClimbNextBar(this.climber));
                final Command climbToSecond = rumbleAfter(new ClimbToSecond(this.climber));
                final Command climbToThird = rumbleAfter(new ClimbToThird(this.climber));

                new JoystickButton(leftStick, NEXT_BAR)
                                .whenPressed(rumbleAfter(climbToNextBar));

                new JoystickButton(leftStick, SECOND_BAR)
                                .whenPressed(climbToSecond);

                new JoystickButton(leftStick, THIRD_BAR)
                                .whenPressed(climbToThird);

                new JoystickButton(leftStick, CANCEL_CLIMB)
                                .whenPressed(new CancelCommand(climbToNextBar, climbToSecond, climbToThird));

                new JoystickButton(rightStick, MANUAL_PARTIAL_EXTEND_HOOKS)
                                .whenPressed(rumbleAfter(new GetToPosition(this.climber, ClimberPositions.partial)));

                new JoystickButton(rightStick, MANUAL_FULL_EXTEND_HOOKS)
                                .whenPressed(rumbleAfter(new GetToPosition(this.climber, ClimberPositions.full)));

                new JoystickButton(driverTwo, SET_HOME)
                                .whenPressed(new InstantCommand(this.climber::setHome, this.climber));

                new JoystickButton(rightStick, MANUAL_SET_HOME_SECONDARY)
                                .whenPressed(new InstantCommand(this.climber::setHome, this.climber));

                new JoystickButton(rightStick, MANUAL_FULL_RETRACT_HOOKS)
                                .whenPressed(rumbleAfter(
                                                new GetToPosition(this.climber, ClimberPositions.bottomFirst)));
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

        /**
         * Completes the transition started in {@link #autoExit()}.
         */
        public void teleopInitialization() {
                this.intake.enterTeleop();
                this.indexer.enterTeleop();
        }

        /**
         * @param rawSpeed the raw speed input from the driver.
         * @param deadband the deadband (this amount on either side of 0) being
         *                 enforced.
         * @return the constrained speed.
         */
        private double enforceDeadband(double rawSpeed, double deadband) {
                return Math.abs(rawSpeed) < deadband ? 0.0 : rawSpeed;
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
