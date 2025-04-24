package commands;

import manager.MusicBandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Команда выполнения скрипта.
 */
public class ExecuteScriptCommand implements Command {
    private MusicBandManager manager;
    private Set<String> executedScripts;
    private static final int MAX_RECURSION_DEPTH = 10;
    private static int currentRecursionDepth = 0;

    /**
     * Конструктор команды ExecuteScriptCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public ExecuteScriptCommand(MusicBandManager manager) {
        this.manager = manager;
        this.executedScripts = new HashSet<>();
    }

    /**
     * Выполняет команду чтения и исполнения скрипта из файла.
     *
     * @param fileName имя файла скрипта
     */
    @Override
    public void execute(String fileName) {
        execute(fileName, null);
    }

    /**
     * Выполняет команду чтения и исполнения скрипта из файла с использованием источника ввода.
     *
     * @param fileName имя файла скрипта
     * @param reader источник ввода данных (может быть null)
     */
    @Override
    public void execute(String fileName, BufferedReader reader) {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Ошибка: Не указано имя файла скрипта.");
            return;
        }


        if (currentRecursionDepth >= MAX_RECURSION_DEPTH) {
            System.out.println("Ошибка: Достигнута максимальная глубина рекурсии (" + MAX_RECURSION_DEPTH + ").");
            return;
        }

        if (executedScripts.contains(fileName)) {
            System.out.println("Скрипт " + fileName + " уже выполняется в текущей цепочке. Прерывание выполнения для предотвращения рекурсии.");
            return;
        }

        executedScripts.add(fileName);
        currentRecursionDepth++;
        System.out.println("Начало выполнения скрипта: " + fileName);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                System.out.println("Выполняется команда: " + line);

                String[] parts = line.split(" ", 2);
                String cmd = parts[0].toLowerCase();
                String argument = parts.length > 1 ? parts[1] : null;

                Command command = manager.getCommands().get(cmd);
                if (command instanceof AddCommand ||
                        command instanceof UpdateCommand ||
                        command instanceof RemoveLowerCommand ||
                        command instanceof AddIfMaxCommand ||
                        command instanceof InsertAtCommand) {
                    command.execute(argument, fileReader);
                } else {
                    manager.executeCommand(line, null);
                }
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