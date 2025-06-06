package lab6.lab.common;

import java.io.Serializable;

/**
 * Класс для представления запроса команды, отправляемого клиентом серверу.
 */
public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String commandName; // Название команды
    private String argument; // Аргумент команды
    private Object object; // Объект (например, MusicBand для команд add, update)

    public CommandRequest(String commandName, String argument, Object object) {
        this.commandName = commandName;
        this.argument = argument;
        this.object = object;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgument() {
        return argument;
    }

    public Object getObject() {
        return object;
    }
}
