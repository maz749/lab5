package commands;

import client.Client;
import common.CommandRequest;
import manager.CommandExecutor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements Command {
    private final CommandExecutor executor;
    private final Client client;
    private final Set<String> executedScripts;
    private static final int MAX_RECURSION_DEPTH = 10;
    private static int currentRecursionDepth = 0;

    public ExecuteScriptCommand(CommandExecutor executor, Client client) {
        this.executor = executor;
        this.client = client;
        this.executedScripts = new HashSet<>();
    }

    @Override
    public void execute(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Ошибка: Не указано имя файла скрипта.");
            return;
        }

        if (currentRecursionDepth >= MAX_RECURSION_DEPTH) {
            System.out.println("Ошибка: Достигнута максимальная глубина рекурсии (" + MAX_RECURSION_DEPTH + ").");
            return;
        }

        if (executedScripts.contains(fileName)) {
            System.out.println("Скрипт " + fileName + " уже выполняется. Прерывание для предотвращения рекурсии.");
            return;
        }

        executedScripts.add(fileName);
        currentRecursionDepth++;
        System.out.println("Начало выполнения скрипта: " + fileName);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                System.out.println("Выполняется команда: " + line);
                client.processScriptCommand(line, fileReader);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
        } finally {
            executedScripts.remove(fileName);
            currentRecursionDepth--;
            System.out.println("Завершение выполнения скрипта: " + fileName);
        }
    }
}