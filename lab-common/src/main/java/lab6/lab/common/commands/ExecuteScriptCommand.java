package lab6.lab.common.commands;

import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ExecuteScriptCommand.class);
    private final CommandExecutor executor;
    private final Set<String> executingScripts;

    public ExecuteScriptCommand(CommandExecutor executor) {
        this.executor = executor;
        this.executingScripts = new HashSet<>();
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("Script filename required for execute_script command");
            throw new IllegalArgumentException("Script filename required for execute_script command");
        }

        String fileName = argument.trim();
        if (executingScripts.contains(fileName)) {
            logger.error("Recursive script execution detected: {}", fileName);
            throw new IllegalStateException("Recursive script execution detected: " + fileName);
        }

        try {
            executingScripts.add(fileName);
            logger.info("Executing script: {}", fileName);
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        logger.debug("Executing command from script: {}", line);
                        executor.executeCommand(line, reader);
                    }
                }
            }
            logger.info("Script execution completed: {}", fileName);
        } catch (IOException e) {
            logger.error("Error reading script file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error reading script file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error executing script {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error executing script: " + e.getMessage(), e);
        } finally {
            executingScripts.remove(fileName);
        }
    }

    @Override
    public String getDescription() {
        return "execute commands from a script file";
    }
}