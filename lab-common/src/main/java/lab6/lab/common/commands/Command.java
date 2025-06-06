package lab6.lab.common.commands;

import java.io.BufferedReader;

public interface Command {
    void execute(String argument);
    default void execute(String argument, BufferedReader reader) {
        execute(argument);
    }
}