package lab6.lab.common.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда для отображения истории последних выполненных команд.
 */
public class HistoryCommand implements Command {
    private static final int MAX_HISTORY_SIZE = 10;
    private static final List<String> history = new ArrayList<>();

    public static void addToHistory(String command) {
        synchronized (history) {
            if (history.size() >= MAX_HISTORY_SIZE) {
                history.remove(0);
            }
            history.add(command);
        }
    }

    @Override
    public void execute(String argument) {
        synchronized (history) {
            if (history.isEmpty()) {
                System.out.println("История команд пуста.");
            } else {
                System.out.println("Последние команды:");
                history.forEach(System.out::println);
            }
        }
    }
}