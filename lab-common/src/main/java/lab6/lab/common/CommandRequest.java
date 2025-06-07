package lab6.lab.common;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String commandName;
    private String argument;
    private Object object;
    private String username;
    private String password;

    public CommandRequest(String commandName, String argument, Object object, String username, String password) {
        this.commandName = commandName;
        this.argument = argument;
        this.object = object;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}