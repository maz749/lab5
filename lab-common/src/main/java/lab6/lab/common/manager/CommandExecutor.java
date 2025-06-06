//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.manager;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.commands.AddCommand;
import lab6.lab.common.commands.AddIfMaxCommand;
import lab6.lab.common.commands.AverageOfNumberOfParticipantsCommand;
import lab6.lab.common.commands.ClearCommand;
import lab6.lab.common.commands.Command;
import lab6.lab.common.commands.ExecuteScriptCommand;
import lab6.lab.common.commands.FilterByNumberOfParticipantsCommand;
import lab6.lab.common.commands.FilterGreaterThanNumberOfParticipantsCommand;
import lab6.lab.common.commands.HelpCommand;
import lab6.lab.common.commands.HistoryCommand;
import lab6.lab.common.commands.InfoCommand;
import lab6.lab.common.commands.InsertAtCommand;
import lab6.lab.common.commands.MaxByNameCommand;
import lab6.lab.common.commands.RemoveAnyByDescriptionCommand;
import lab6.lab.common.commands.RemoveByIdCommand;
import lab6.lab.common.commands.RemoveHeadCommand;
import lab6.lab.common.commands.RemoveLowerCommand;
import lab6.lab.common.commands.SaveCommand;
import lab6.lab.common.commands.ShowCommand;
import lab6.lab.common.commands.SortCommand;
import lab6.lab.common.commands.UpdateCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandExecutor {
    private static final Logger logger = LogManager.getLogger(CommandExecutor.class);
    private final Map<String, Command> commands = new HashMap();

    public CommandExecutor(MusicBandCollection collection, FileStorage storage) {
        this.registerCommands(collection, storage);
        logger.info("Команды зарегистрированы. Всего команд: {}", this.commands.size());
    }

    private void registerCommands(MusicBandCollection collection, FileStorage storage) {
        this.commands.put("add", new AddCommand(collection));
        this.commands.put("add_if_max", new AddIfMaxCommand(collection));
        this.commands.put("remove_by_id", new RemoveByIdCommand(collection));
        this.commands.put("clear", new ClearCommand(collection));
        this.commands.put("execute_script", new ExecuteScriptCommand(this));
        this.commands.put("filter_by_number_of_participants", new FilterByNumberOfParticipantsCommand(collection));
        this.commands.put("help", new HelpCommand());
        this.commands.put("info", new InfoCommand(collection));
        this.commands.put("max_by_name", new MaxByNameCommand(collection));
        this.commands.put("remove_any_by_description", new RemoveAnyByDescriptionCommand(collection));
        this.commands.put("remove_head", new RemoveHeadCommand(collection));
        this.commands.put("remove_lower", new RemoveLowerCommand(collection));
        this.commands.put("save", new SaveCommand(storage));
        this.commands.put("show", new ShowCommand(collection));
        this.commands.put("update", new UpdateCommand(collection));
        this.commands.put("insert_at", new InsertAtCommand(collection));
        this.commands.put("sort", new SortCommand(collection));
        this.commands.put("history", new HistoryCommand());
        this.commands.put("average_of_number_of_participants", new AverageOfNumberOfParticipantsCommand(collection));
        this.commands.put("filter_greater_than_number_of_participants", new FilterGreaterThanNumberOfParticipantsCommand(collection));
    }

    public Map<String, Command> getCommands() {
        return this.commands;
    }

    public void executeCommand(CommandRequest request) {
        String cmd = request.getCommandName().toLowerCase();
        Command command = (Command)this.commands.get(cmd);
        if (command == null) {
            logger.warn("Попытка выполнить неизвестную команду: {}", cmd);
            throw new IllegalArgumentException("Неизвестная команда: " + cmd);
        } else if (cmd.equals("save")) {
            logger.warn("Попытка выполнить команду save на клиенте");
            throw new IllegalArgumentException("Команда save доступна только на сервере.");
        } else {
            logger.info("Выполнение команды: {}", cmd);
            HistoryCommand.addToHistory(cmd);

            try {
                command.execute(request.getArgument());
                logger.debug("Команда {} выполнена успешно", cmd);
            } catch (Exception var5) {
                Exception e = var5;
                logger.error("Ошибка при выполнении команды {}: {}", cmd, e.getMessage());
                throw new RuntimeException("Ошибка выполнения команды " + cmd + ": " + e.getMessage(), e);
            }
        }
    }

    public void executeCommand(String commandLine, BufferedReader reader) {
        String[] parts = commandLine.trim().split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;
        Command command = (Command)this.commands.get(cmd);
        if (command == null) {
            logger.warn("Неизвестная команда введена пользователем: {}", cmd);
            System.out.println("Неизвестная команда: " + cmd + ". Введите 'help' для списка команд.");
        } else {
            logger.info("Выполнение команды через строку: {}", cmd);
            HistoryCommand.addToHistory(cmd);

            try {
                if (cmd.equals("execute_script")) {
                    ((ExecuteScriptCommand)command).execute(argument, reader, (Object)null);
                } else {
                    command.execute(argument, reader);
                }

                logger.debug("Команда {} выполнена успешно", cmd);
            } catch (Exception var8) {
                Exception e = var8;
                logger.error("Ошибка при выполнении команды {}: {}", cmd, e.getMessage());
                System.out.println("Ошибка выполнения команды " + cmd + ": " + e.getMessage());
            }

        }
    }
}
