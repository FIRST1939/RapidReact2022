// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.state;

import java.util.function.UnaryOperator;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * An extension of {@link RandomAccessCommandGroup} that wraps a Java
 * <code>enum</code> around the index values (the enum ordinal values). No
 * additional functionality is provided; however, using an enumerated type can
 * often make deisgn and implementation of more complex state machines easier to
 * complete, discuss, and comprehend.
 */
public class EnumeratedRandomAccessCommandGroup<E extends Enum<E>> extends RandomAccessCommandGroup {
    private final Class<E> m_enumClazz;
    private final E[] m_enumValues;
    private final UnaryOperator<E> m_nextCommandStateOperator;

    /**
     * Creates a new EnumeratedRandomAccessCommandGroup. The given commands will be
     * run in a arbitrary order, with the CommandGroup finishing when no command is
     * selected to run next.
     * 
     * <p>
     * The only time a contained command's end() method receives true for the
     * interrupted parameter is when it is the current command and the entire group
     * is interrupted. All other contained command end() calls will be due to the
     * contained command's isFinish() returning true, and then the end() interrupted
     * parameter will be false. One important effect of this is that the interrupted
     * parameter cannot be used as reliable input into selecting the next command.
     * The isFinished() method must properly detect the time for a command change.
     * The selection of the next command is left to the
     * <code>nextCommandStateOperator</code>.
     * 
     * <p>
     * Typically, if the nextCommandStateOperator returns null, this command group
     * ends. However, this command is designed to work properly with
     * {@link PerpetualCommand} (see {@link #perpetually()}). In that case, it will
     * not end, but will call the nextCommandStateOperator during each execute call
     * with a parameter of null.
     * 
     * <p>
     * The most common case for running this group perpetually is as a default
     * command for a subsystem and this group implements a state machine for the
     * subsystem.
     *
     * @param enumClazz                the class of the Java <code>enum</code> the
     *                                 defines the set of states for the state
     *                                 machine being implemented.
     * @param nextCommandStateOperator an operator that takes the current state as a
     *                                 parameter and returns the next state. It
     *                                 cannot be null.
     * @param commands                 the commands to include in this group.
     */
    public EnumeratedRandomAccessCommandGroup(
            final Class<E> enumClazz,
            final UnaryOperator<E> nextCommandStateOperator,
            final Command... commands) {
        // Would prefer to send an IntUnaryOperator on super call, but that would
        // require an instance method reference during construction which is not
        // allowed. See the getNextCommandIndex() override instead.
        super(commands);
        // TODO add null checks (all parms) in wpilib style.
        m_enumClazz = enumClazz;
        m_enumValues = m_enumClazz.getEnumConstants();
        m_nextCommandStateOperator = nextCommandStateOperator;
    }

    @Override
    public void addCommands(final Command... commands) {
        throw new UnsupportedOperationException("Cannot add commands to a group mapped to an enumeration.");
    }

    /**
     * @param nextInitialState the {@link E} to use the next time the state machine
     *                         is initialized.
     */
    public void setInitialState(final E nextInitialState) {
        setInitialCommandIndex(
                nextInitialState == null
                        ? RandomAccessCommandGroup.NO_CURRENT_COMMAND
                        : nextInitialState.ordinal());
    }

    /**
     * @return the currently active {@link E} for the state machine. This will
     *         be null if the state machine is not running. It can be null if the
     *         running state machine is between states.
     */
    public E getCurrentState() {
        if (this.isCurrentCommandIndexInRange()) {
            return m_enumValues[this.getCurrentCommandIndex()];
        }
        return null;
    }

    @Override
    protected int getNextCommandIndex() {
        final int current = getCurrentCommandIndex();
        final E currentState = current >= 0 && current < m_enumValues.length
                ? m_enumValues[current]
                : null;
        final E nextState = m_nextCommandStateOperator.apply(currentState);
        return nextState != null
                ? nextState.ordinal()
                : NO_CURRENT_COMMAND;
    }
}
