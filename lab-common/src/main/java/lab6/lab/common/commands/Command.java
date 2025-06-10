package lab6.lab.common.commands;

import java.io.BufferedReader;

public interface Command {
    /**
     * Executes the command with the given argument and object
     * @param argument The command argument (can be null)
     * @param object The command object (can be null)
     */
    void execute(String argument, Object object);

    /**
     * @return A description of what the command does
     */
    String getDescription();

    default void execute(String argument) {
        execute(argument, null);
    }

    default void execute(String argument, BufferedReader reader) {
        execute(argument, null);
    }
}