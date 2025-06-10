package lab6.lab.common.manager;

import lab6.lab.common.commands.*;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.database.MusicBandRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private static final Logger logger = LogManager.getLogger(CommandExecutor.class);
    private final Map<String, Command> commands;

    public CommandExecutor(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.commands = new HashMap<>();
        registerCommands(collection, musicBandRepository);
        logger.info("Commands registered. Total commands: {}", commands.size());
    }

    private void registerCommands(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand(collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("add", new AddCommand(collection, musicBandRepository));
        commands.put("update", new UpdateCommand(collection, musicBandRepository));
        commands.put("remove_by_id", new RemoveByIdCommand(collection, musicBandRepository));
        commands.put("clear", new ClearCommand(collection, musicBandRepository));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("add_if_max", new AddIfMaxCommand(collection, musicBandRepository));
        commands.put("remove_lower", new RemoveLowerCommand(collection, musicBandRepository));
        commands.put("history", new HistoryCommand());
        commands.put("remove_head", new RemoveHeadCommand(collection));
        commands.put("remove_any_by_description", new RemoveAnyByDescriptionCommand(collection));
        commands.put("max_by_name", new MaxByNameCommand(collection));
        commands.put("average_of_number_of_participants", new AverageOfNumberOfParticipantsCommand(collection));
        commands.put("filter_by_number_of_participants", new FilterByNumberOfParticipantsCommand(collection));
    }

    public void executeCommand(CommandRequest request) {
        String commandName = request.getCommandName().toLowerCase();
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(request.getArgument(), request.getObject());
        } else {
            logger.error("Unknown command: {}", commandName);
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
    }

    public String getHelp() {
        StringBuilder help = new StringBuilder("Available commands:\n");
        commands.forEach((name, command) -> help.append(name).append(": ").append(command.getDescription()).append("\n"));
        return help.toString();
    }

    public Map<String, Command> getCommands() {
        return commands;
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
                ((ExecuteScriptCommand) command).execute(argument, reader);
            } else {
                command.execute(argument, null);
            }
            logger.debug("Команда {} выполнена успешно", cmd);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды {}: {}", cmd, e.getMessage());
            System.out.println("Ошибка выполнения команды " + cmd + ": " + e.getMessage());
        }
    }
}