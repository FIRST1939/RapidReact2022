// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.state;

import java.util.function.UnaryOperator;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;

/**
 * An extension of {@link RandomAccessCommandGroup} that wraps a Java
 * <code>enum</code> around the index values (the enum ordinal values). No
 * additional functionality is provided; however, using an enumerated type can
 * often make deisgn and implementation of more complex state machines easier to
 * complete, discuss, and comprehend.
 * 
 * <p>
 * As a rule, CommandGroups require the union of the requirements of their
 * component commands.
 *
 * <p>
 * This class is provided by the NewCommands VendorDep TODO
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
     * <p>
     * The number of enumerators in the enum class and the number of commands added
     * via the constructor and/or the addCommands method, SHOULD be the same. This
     * is not enforced. If there are fewer commands than enumerators, selecting one
     * of the unmapped enumerators will result in no command being selected and the
     * ending of the command group. If there are more commands than enumerators, the
     * extra commands (latest ones added) are included in the command group but
     * effectively ignored.
     *
     * @param enumClazz                the class of the Java <code>enum</code> the
     *                                 defines the set of states for the state
     *                                 machine being implemented.
     * @param nextCommandStateOperator an operator that takes the current state as a
     *                                 parameter and returns the next state. If
     *                                 null, a operator that returns the next enum
     *                                 value is used. This operator returns null if
     *                                 the end of the enumeration has been reached.
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
        m_nextCommandStateOperator = nextCommandStateOperator != null ? nextCommandStateOperator : this::nextEnumValue;
    }

    /**
     * Same as
     * {@link #EnumeratedRandomAccessCommandGroup(Class, UnaryOperator, Command...)}
     * with a null operator.
     * 
     * @param enumClazz the class of the Java <code>enum</code> the defines the set
     *                  of states for the state machine being implemented.
     * @param commands  the commands to include in this group.
     */
    public EnumeratedRandomAccessCommandGroup(
            final Class<E> enumClazz,
            final Command... commands) {
        this(enumClazz, null, commands);
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

    /**
     * Uses the constructor provide next command state operator to determine the
     * next enumerator and set its ordinal as the next command index.
     */
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

    /**
     * Implemenation of the default next enum value operator for getting the next
     * state enum.
     * 
     * @param e the current enum value.
     * @return the next enum value or null if e is null or the last enum value.
     */
    private E nextEnumValue(E e) {
        if (e != null) {
            int nextOrdinal = e.ordinal() + 1;
            if (nextOrdinal < m_enumValues.length) {
                return m_enumValues[nextOrdinal];
            }
        }
        return null;
    }
}
