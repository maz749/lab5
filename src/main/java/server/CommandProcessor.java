package server;

import commands.*;
import common.CommandRequest;
import common.CommandResponse;
import manager.CommandExecutor;
import manager.MusicBandManager;
import models.MusicBand;

import java.util.List;
import java.util.stream.Collectors;

public class CommandProcessor {
    private final MusicBandManager manager;

    public CommandProcessor(MusicBandManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("MusicBandManager не может быть null.");
        }
        this.manager = manager;
    }

    public CommandResponse processRequest(CommandRequest request) {
        try {
            String command = request.getCommandName().toLowerCase();
            String argument = request.getArgument();
            Object object = request.getObject();

            // Запрет выполнения execute_script на сервере
            if (command.equals("execute_script")) {
                return new CommandResponse("Ошибка: Команда execute_script не поддерживается на сервере.", null, false);
            }

            // Команды, возвращающие список
            if (command.equals("show") || command.equals("filter_greater_than_number_of_participants")) {
                List<MusicBand> bands;
                if (command.equals("filter_greater_than_number_of_participants")) {
                    if (argument == null || argument.trim().isEmpty()) {
                        return new CommandResponse("Ошибка: Требуется указать количество участников.", null, false);
                    }
                    try {
                        int numberOfParticipants = Integer.parseInt(argument);
                        if (numberOfParticipants <= 0) {
                            return new CommandResponse("Ошибка: Количество участников должно быть больше 0.", null, false);
                        }
                        bands = manager.getMusicBands().stream()
                                .filter(band -> band.getNumberOfParticipants() > numberOfParticipants)
                                .sorted()
                                .collect(Collectors.toList());
                    } catch (NumberFormatException e) {
                        return new CommandResponse("Ошибка: Количество участников должно быть числом.", null, false);
                    }
                } else {
                    bands = manager.getMusicBands();
                }
                return new CommandResponse("Команда выполнена", bands, true);
            }

            // Проверка наличия объекта для команд, требующих MusicBand
            if (requiresObject(command)) {
                if (!(object instanceof MusicBand)) {
                    return new CommandResponse("Ошибка: Команда " + command + " требует объект MusicBand.", null, false);
                }
                MusicBand band = (MusicBand) object;
                // Валидация объекта
                try {
                    validateMusicBand(band);
                } catch (IllegalArgumentException e) {
                    return new CommandResponse("Ошибка: Некорректный объект MusicBand: " + e.getMessage(), null, false);
                }
                CommandExecutor executor = manager.getExecutor();
                if (executor == null) {
                    return new CommandResponse("Ошибка: CommandExecutor не инициализирован.", null, false);
                }
                Command cmd = executor.getCommands().get(command);
                if (cmd == null) {
                    return new CommandResponse("Ошибка: Команда " + command + " не найдена.", null, false);
                }

                // Выполнение команд с объектом
                switch (command) {
                    case "add":
                        if (!(cmd instanceof AddCommand)) {
                            return new CommandResponse("Ошибка: Команда add имеет неверный тип.", null, false);
                        }
                        ((AddCommand) cmd).execute(band);
                        return new CommandResponse("Музыкальная группа успешно добавлена.", null, true);
                    case "add_if_max":
                        if (!(cmd instanceof AddIfMaxCommand)) {
                            return new CommandResponse("Ошибка: Команда add_if_max имеет неверный тип.", null, false);
                        }
                        ((AddIfMaxCommand) cmd).execute(band);
                        return new CommandResponse("Команда add_if_max выполнена.", null, true);
                    case "update":
                        if (!(cmd instanceof UpdateCommand)) {
                            return new CommandResponse("Ошибка: Команда update имеет неверный тип.", null, false);
                        }
                        if (argument == null || argument.trim().isEmpty()) {
                            return new CommandResponse("Ошибка: Требуется указать ID.", null, false);
                        }
                        try {
                            int id = Integer.parseInt(argument);
                            ((UpdateCommand) cmd).execute(id, band);
                            return new CommandResponse("Музыкальная группа успешно обновлена.", null, true);
                        } catch (NumberFormatException e) {
                            return new CommandResponse("Ошибка: ID должен быть числом.", null, false);
                        }
                    case "remove_lower":
                        if (!(cmd instanceof RemoveLowerCommand)) {
                            return new CommandResponse("Ошибка: Команда remove_lower имеет неверный тип.", null, false);
                        }
                        ((RemoveLowerCommand) cmd).execute(band);
                        return new CommandResponse("Команда remove_lower выполнена.", null, true);
                    case "insert_at":
                        if (!(cmd instanceof InsertAtCommand)) {
                            return new CommandResponse("Ошибка: Команда insert_at имеет неверный тип.", null, false);
                        }
                        if (argument == null || argument.trim().isEmpty()) {
                            return new CommandResponse("Ошибка: Требуется указать индекс.", null, false);
                        }
                        try {
                            int index = Integer.parseInt(argument);
                            ((InsertAtCommand) cmd).execute(index, band);
                            return new CommandResponse("Команда insert_at выполнена.", null, true);
                        } catch (NumberFormatException e) {
                            return new CommandResponse("Ошибка: Индекс должен быть числом.", null, false);
                        }
                    default:
                        return new CommandResponse("Ошибка: Неизвестная команда " + command, null, false);
                }
            }

            // Обработка остальных команд
            manager.executeCommand(buildCommandLine(command, argument));
            return new CommandResponse("Команда успешно выполнена", null, true);
        } catch (OutOfMemoryError e) {
            return new CommandResponse("Ошибка: Недостаточно памяти для обработки запроса. Попробуйте уменьшить размер данных.", null, false);
        } catch (Exception e) {
            return new CommandResponse("Ошибка: " + e.getMessage(), null, false);
        }
    }

    private boolean requiresObject(String command) {
        return command.equals("add") || command.equals("add_if_max") ||
                command.equals("update") || command.equals("remove_lower") ||
                command.equals("insert_at");
    }

    private void validateMusicBand(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        if (band.getName() == null || band.getName().isEmpty()) {
            throw new IllegalArgumentException("Имя группы не может быть пустым.");
        }
        if (band.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты не могут быть null.");
        }
        if (band.getNumberOfParticipants() < 0) {
            throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
        }
        if (band.getGenre() == null) {
            throw new IllegalArgumentException("Жанр не может быть null.");
        }
        if (band.getBestAlbum() == null || band.getBestAlbum().getLength() <= 0) {
            throw new IllegalArgumentException("Альбом должен быть корректным с длиной больше 0.");
        }
        if (Math.abs(band.getCoordinates().getX()) > 1_000_000) {
            throw new IllegalArgumentException("Координата X слишком большая.");
        }
        if (Math.abs(band.getCoordinates().getY()) > 1_000_000) {
            throw new IllegalArgumentException("Координата Y слишком большая.");
        }
        if (band.getNumberOfParticipants() > 1_000_000) {
            throw new IllegalArgumentException("Количество участников слишком большое.");
        }
        if (band.getBestAlbum().getLength() > 1_000_000) {
            throw new IllegalArgumentException("Длина альбома слишком большая.");
        }
    }

    private String buildCommandLine(String command, String argument) {
        return argument != null ? command + " " + argument : command;
    }
}