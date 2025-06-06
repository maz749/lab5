package lab6.lab.common.commands;

import java.util.LinkedList;
import java.util.Queue;

public class HistoryCommand implements lab6.lab.common.commands.Command {
    private static final int MAX_HISTORY_SIZE = 12;
    private static final Queue<String> commandHistory = new LinkedList<>();

    public static void addToHistory(String command) {
        if (commandHistory.size() >= MAX_HISTORY_SIZE) {
            commandHistory.poll();
        }
        commandHistory.offer(command);
    }

    @Override
    public void execute(String argument) {
        System.out.println("История последних " + MAX_HISTORY_SIZE + " команд:");
        if (commandHistory.isEmpty()) {
            System.out.println("История пуста.");
        } else {
            for (String cmd : commandHistory) {
                System.out.println(cmd);
            }
        }
    }
}