package lab6.lab.common.commands;

import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда для отображения истории последних выполненных команд.
 */
public class HistoryCommand implements Command {
    private static final Logger logger = LogManager.getLogger(HistoryCommand.class);
    private static final int MAX_HISTORY_SIZE = 10;
    private static final List<String> history = new ArrayList<>();

    public static void addToHistory(String command) {
        synchronized (history) {
            if (history.size() >= MAX_HISTORY_SIZE) {
                history.remove(0);
            }
            history.add(command);
            logger.debug("Added command to history: {}", command);
        }
    }

    @Override
    public void execute(String argument, Object object) {
        synchronized (history) {
            if (history.isEmpty()) {
                logger.info("Command history is empty");
                System.out.println("История команд пуста.");
            } else {
                logger.info("Showing {} commands from history", history.size());
                System.out.println("Последние команды:");
                history.forEach(System.out::println);
            }
        }
    }

    public CommandResponse executeWithResponse() {
        synchronized (history) {
            if (history.isEmpty()) {
                logger.info("Command history is empty");
                return new CommandResponse("Command history is empty.", null, true);
            } else {
                logger.info("Returning {} commands from history", history.size());
                return new CommandResponse("Last commands:", new ArrayList<>(history), true);
            }
        }
    }

    @Override
    public String getDescription() {
        return "show the last executed commands";
    }
}