package manager;

import commands.*;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для выполнения команд.
 */
public class CommandExecutor {
    private final Map<String, Command> commands;

    public CommandExecutor(MusicBandCollection collection, FileStorage storage) {
        this.commands = new HashMap<>();
        registerCommands(collection, storage);
    }

    private void registerCommands(MusicBandCollection collection, FileStorage storage) {
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
        commands.put("save", new SaveCommand(storage, collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("update", new UpdateCommand(collection));
        commands.put("insert_at", new InsertAtCommand(collection));
        commands.put("sort", new SortCommand(collection));
        commands.put("history", new HistoryCommand());
        commands.put("average_of_number_of_participants", new AverageOfNumberOfParticipantsCommand(collection));
        commands.put("filter_greater_than_number_of_participants", new FilterGreaterThanNumberOfParticipantsCommand(collection));
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
            System.out.println("Неизвестная команда: " + cmd);
            return;
        }

        HistoryCommand.addToHistory(cmd);
        if (reader != null) {
            command.execute(argument, reader);
        } else {
            command.execute(argument);
        }
    }
}