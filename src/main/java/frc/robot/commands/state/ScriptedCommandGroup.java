// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.state;

import edu.wpi.first.wpilibj2.command.Command;

/** Add your docs here. */
public class ScriptedCommandGroup extends RandomAccessCommandGroup {
    public static final int[] EMPTY_SCRIPT = {};
    private int[] m_script;
    private int m_currentScriptIndex = RandomAccessCommandGroup.NO_CURRENT_COMMAND;

    public ScriptedCommandGroup(final Command... commands) {
        // Would prefer to send an IntUnaryOperator on super call, but that would
        // require an instance method reference during construction which is not
        // allowed. See the getNextCommandIndex() override instead.
        super(commands);
    }

    @Override
    protected int getNextCommandIndex() {
        m_currentScriptIndex++;
        if (m_currentScriptIndex >= m_script.length) {
            m_currentScriptIndex = RandomAccessCommandGroup.NO_CURRENT_COMMAND;
            return RandomAccessCommandGroup.NO_CURRENT_COMMAND;
        }
        return m_script[m_currentScriptIndex];
    }

    public void setScript(final int[] script) {
        if (isScheduled()) {
            throw new IllegalStateException("Cannot change the script while running.");
        }
        m_script = script;
        if (m_script == null) {
            m_script = EMPTY_SCRIPT;
        }
    }

    @Override
    public void setInitialCommandIndex(final int initialCommandIndex) {
    }
}
