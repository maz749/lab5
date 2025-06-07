package lab6.lab.common.manager;

import lab6.lab.common.commands.*;
import lab6.lab.common.CommandRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private static final Logger logger = LogManager.getLogger(CommandExecutor.class);
    private final Map<String, Command> commands;

    public CommandExecutor(MusicBandCollection collection, DatabaseManager dbManager) {
        this.commands = new HashMap<>();
        registerCommands(collection, dbManager);
        logger.info("Команды зарегистрированы. Всего команд: {}", commands.size());
    }

    private void registerCommands(MusicBandCollection collection, DatabaseManager dbManager) {
        commands.put("add", new AddCommand(collection));
        commands.put("add_if_max", new AddIfMaxCommand(collection));
        commands.put("remove_by_id", new RemoveByIdCommand(collection));
        commands.put("clear", new ClearCommand(collection));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("filter_by_number_of_participants", new FilterByNumberOfParticipantsCommand(collection));
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand(collection));
        commands.put("max_by_name", new MaxByNameCommand(collection));
        commands.put("remove_any_by_description", new RemoveAnyByDescriptionCommand(collection));
        commands.put("remove_head", new RemoveHeadCommand(collection));
        commands.put("remove_lower", new RemoveLowerCommand(collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("update", new UpdateCommand(collection));
        commands.put("insert_at", new InsertAtCommand(collection));
        commands.put("sort", new SortCommand(collection));
        commands.put("history", new HistoryCommand());
        commands.put("average_of_number_of_participants", new AverageOfNumberOfParticipantsCommand(collection));
        commands.put("filter_greater_than_number_of_participants", new FilterGreaterThanNumberOfParticipantsCommand(collection));
        commands.put("login", new LoginCommand(dbManager));
        commands.put("register", new RegisterCommand(dbManager));
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void executeCommand(CommandRequest request) {
        String cmd = request.getCommandName().toLowerCase();
        Command command = commands.get(cmd);
        if (command == null) {
            logger.warn("Попытка выполнить неизвестную команду: {}", cmd);
            throw new IllegalArgumentException("Неизвестная команда: " + cmd);
        }

        logger.info("Выполнение команды: {}", cmd);
        HistoryCommand.addToHistory(cmd);
        try {
            command.execute(request.getArgument());
            logger.debug("Команда {} выполнена успешно", cmd);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды {}: {}", cmd, e.getMessage());
            throw new RuntimeException("Ошибка выполнения команды " + cmd + ": " + e.getMessage(), e);
        }
    }

    public void executeCommand(String commandLine, BufferedReader reader) {
        String[] parts = commandLine.trim().split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        Command command = commands.get(cmd);
        if (command == null) {
            logger.warn("Неизвестная команда введена пользователем: {}", cmd);
            System.out.println("Неизвестная команда: " + cmd + ". Введите 'help' для списка команд.");
            return;
        }

        logger.info("Выполнение команды через строку: {}", cmd);
        HistoryCommand.addToHistory(cmd);
        try {
            if (cmd.equals("execute_script")) {
                ((ExecuteScriptCommand) command).execute(argument, reader, null);
            } else {
                command.execute(argument, reader);
            }
            logger.debug("Команда {} выполнена успешно", cmd);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды {}: {}", cmd, e.getMessage());
            System.out.println("Ошибка выполнения команды " + cmd + ": " + e.getMessage());
        }
    }
}