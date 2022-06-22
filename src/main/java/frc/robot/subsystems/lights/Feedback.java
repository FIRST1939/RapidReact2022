// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.lights;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.Devices;
import frc.robot.devices.RobotCargoCount;
import frc.robot.devices.Targeting.TargetingType;
import frc.robot.subsystems.drive.DriveTrain;
import frc.robot.subsystems.drive.MoveToCargoRing;
import frc.robot.subsystems.drive.TurnToTarget;
import frc.robot.subsystems.drive.TurnToTargetThenMoveToCargoRing;
import frc.robot.subsystems.intake.manual.ManualIntakeRollerBelts;
import frc.robot.subsystems.shooter.Shooter;

/**
 * This enumeration defines the feedback values used by the {@link Lights}
 * subsystem. It also provides the {@link #evaluate()} method to evaluate the
 * current robot state. See the {@link #evaluate()} method javadoc for details.
 */
public enum Feedback {
    BLING(LEDMode.FIRE, () -> true),
    INTAKING_EMPTY(
            LEDMode.PINK,
            () -> !Lights.getInstance().getIntake().isRetracted()
                    && RobotCargoCount.getInstance().isEmpty()),
    INTAKING_NOT_EMPTY_NOT_FULL(
            LEDMode.WHITE,
            () -> !Lights.getInstance().getIntake().isRetracted()
                    && !RobotCargoCount.getInstance().isEmpty()
                    && !RobotCargoCount.getInstance().isFull()),
    ROBOT_FULL(
            LEDMode.CONFETTI,
            () -> Lights.getInstance().getIntake().isRetracted()
                    && RobotCargoCount.getInstance().isFull()),
    INTAKE_INDEXER_MANUAL(
            LEDMode.YELLOW,
            Feedback::isIntakeManual),
    TARGETING_IN_PROGRESS(
            LEDMode.RED,
            Feedback::evaluateTargetingInProgress),
    TARGETING_DONE(
            LEDMode.GREEN,
            Feedback::evaluateTargetingDone);

    private static Feedback values[] = values();
    private final LEDMode ledMode;
    private final BooleanSupplier activeWhen;

    /**
     * @param ledMode    the color pattern to use for this feedback.
     * @param activeWhen when this feedback is to be considered active.
     */
    private Feedback(final LEDMode ledMode, final BooleanSupplier activeWhen) {
        this.ledMode = ledMode;
        this.activeWhen = activeWhen;
    }

    /**
     * @return the color pattern to use for this feedback.
     */
    public LEDMode getLEDMode() {
        return this.ledMode;
    }

    /**
     * @return true if this feedback is to be considered active.
     */
    public boolean isActive() {
        return this.activeWhen.getAsBoolean();
    }

    /**
     * Evaluates the feedback enumerators from last to first. The first enumerator
     * to evaluate to true is returned. Note that the enumerator at ordinal 0 (last
     * to be evaluated) always returns true.
     * 
     * @return the highest priority active feedback.
     */
    public static Feedback evaluate() {
        int i = values.length;
        do {
            i--;
        } while (!values[i].isActive());
        return values[i];
    }

    /**
     * A helper method to keep the feedback declaration more tidy.
     * 
     * @return true if the intake and indexer system is running manually.
     */
    private static boolean isIntakeManual() {
        return Lights.getInstance().getIntake().getCurrentCommand() instanceof ManualIntakeRollerBelts;
    }

    /**
     * A helper method to keep the feedback declaration more tidy.
     * 
     * @return true if shooter targeting is on, intake is neither active nor manual,
     *         and we have cargo; however, we are not yet on target.
     */
    private static boolean evaluateTargetingInProgress() {
        final Lights lights = Lights.getInstance();
        return shouldConsiderTargeting(lights)
                && targetingInProgress(lights);
    }

    /**
     * A helper method to keep the feedback declaration more tidy.
     * 
     * @return true if shooter targeting is on, intake is neither active nor manual,
     *         and we have cargo; plus, we are on target.
     */
    private static boolean evaluateTargetingDone() {
        final Lights lights = Lights.getInstance();
        return shouldConsiderTargeting(lights)
                && !targetingInProgress(lights);
    }

    /**
     * @param lights the {@link Lights} instance to use in evaluation (to obtain
     *               subsystem instances).
     * 
     * @return true if shooter targeting is on, intake is neither active nor manual,
     *         and we have cargo.
     */
    private static boolean shouldConsiderTargeting(final Lights lights) {
        return lights.getShooter().getTargeting().getType() != TargetingType.OFF
                && !RobotCargoCount.getInstance().isEmpty()
                && lights.getIntake().isRetracted()
                && !isIntakeManual();
    }

    /**
     * @param lights the {@link Lights} instance to use in evaluation (to obtain
     *               subsystem instances).
     * 
     * @return true if the robot is current manuvering to target or the shooter is
     *         not ready.
     */
    private static boolean targetingInProgress(final Lights lights) {
        final DriveTrain driveTrain = lights.getDriveTrain();
        final Shooter shooter = lights.getShooter();
        final Command driveTrainCommand = driveTrain.getCurrentCommand();
        return driveTrainCommand instanceof TurnToTarget
                || driveTrainCommand instanceof MoveToCargoRing
                || driveTrainCommand instanceof TurnToTargetThenMoveToCargoRing
                || !shooter.getTargeting().isTargetFound()
                || Math.abs(shooter.getTargeting().getHorizontalAngleError()) > Devices.ALIGNED_TO_HUB
                || !shooter.isShooterReady();
    }
}
