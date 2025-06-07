package lab6.lab.common.commands;

import lab6.lab.common.manager.CommandExecutor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements Command {
    private final CommandExecutor executor;
    private final Set<String> executingScripts;

    public ExecuteScriptCommand(CommandExecutor executor) {
        this.executor = executor;
        this.executingScripts = new HashSet<>();
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда execute_script требует имя файла.");
    }

    public void execute(String fileName, BufferedReader mainReader, String[] auth) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Ошибка: Не указано имя файла скрипта.");
            return;
        }
        if (executingScripts.contains(fileName)) {
            System.out.println("Ошибка: Скрипт " + fileName + " уже выполняется (рекурсия обнаружена).");
            return;
        }
        executingScripts.add(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.toLowerCase().startsWith("execute_script")) {
                    System.out.println("Ошибка: Вложенные вызовы execute_script не поддерживаются.");
                    continue;
                }
                System.out.println("Выполняется: " + line);
                executor.executeCommand(line, reader);
            }
            System.out.println("Скрипт " + fileName + " выполнен.");
        } catch (IOException e) {
            System.out.println("Ошибка при выполнении скрипта " + fileName + ": " + e.getMessage());
        } finally {
            executingScripts.remove(fileName);
        }
    }
}