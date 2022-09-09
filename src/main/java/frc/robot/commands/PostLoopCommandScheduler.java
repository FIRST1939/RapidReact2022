package frc.robot.commands;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class PostLoopCommandScheduler {

    private static Set<Command> toSchedule = new LinkedHashSet<>();
    private PostLoopCommandScheduler() {}

    /**
     * From the end(boolean) method of a state machine commmand, call this to
     * schedule the next state.
     * 
     * @param cmd the command for the next state.
     */
    public static void addCommandToSchedule (final Command command) {

        if (command != null) {

            toSchedule.add(command);
        }
    }

    /**
     * Called from {@link Robot#robotPeriodic()} after the sceduler run to schedule
     * the next state commands.
     */
    public static void scheduleCommands () {

        // Create copy to avoid concurrant mod exceptions.
        final List<Command> toScheduleCopy = new ArrayList<>(toSchedule);
        toSchedule.clear();
        toScheduleCopy.forEach(Command::schedule);
    }
}
