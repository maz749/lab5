/**
 * Команда для вывода последних 12 выполненных команд.
 */
package commands;

import java.util.LinkedList;
import java.util.Queue;

public class HistoryCommand implements Command {
    private static final int MAX_HISTORY_SIZE = 12;
    private static Queue<String> commandHistory = new LinkedList<>();

    /**
     * Добавляет команду в историю.
     *
     * @param command имя команды
     */
    public static void addToHistory(String command) {
        if (commandHistory.size() >= MAX_HISTORY_SIZE) {
            commandHistory.poll();
        }
        commandHistory.offer(command);
    }

    /**
     * Выполняет команду вывода истории команд.
     *
     * @param argument аргумент команды (не используется)
     */
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