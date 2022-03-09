// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

/**
 * WPILIB gets caught in a infinite loop if you try to schecule a command from
 * the end (with interruped true) of another command and the two commands use
 * the same subsystem. This is not unusual in our state machines. This class
 * helps us work around the issue.
 */
public class PostLoopCommandScheduler {
    private static List<Command> toSchedule = new ArrayList<>();

    /**
     * No instances of static utility class.
     */
    private PostLoopCommandScheduler() {
    }

    /**
     * From the end(boolean) method of a state machine commmand, call this to
     * schedule the next state.
     * 
     * @param cmd the command for the next state.
     */
    public static void addCommandToSchedule(final Command cmd) {
        if (cmd != null) {
            toSchedule.add(cmd);
        }
    }

    /**
     * Called from {@link Robot#robotPeriodic()} after the sceduler run to schedule
     * the next state commands.
     */
    public static void scheduleCommands() {
        toSchedule.forEach(Command::schedule);
        toSchedule.clear();
    }
}
