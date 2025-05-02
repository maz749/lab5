package client;

import common.CommandRequest;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 * Класс для парсинга команд, введенных пользователем, и создания объектов CommandRequest.
 */
public class CommandParser {
    private final Scanner scanner;
    private static final int MAX_NAME_LENGTH = 1000; // Ограничение на длину имени

    public CommandParser() {
        this.scanner = new Scanner(System.in);
    }

    public CommandRequest parseCommand(String input, BufferedReader scriptReader) throws IOException {
        String[] parts = input.trim().split(" ", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        if (command.equals("save")) {
            System.out.println("Команда 'save' недоступна на клиенте.");
            return null;
        }

        if (requiresObject(command)) {
            MusicBand band = createMusicBand(scriptReader);
            if (band == null) return null;
            return new CommandRequest(command, argument, band);
        }

        return new CommandRequest(command, argument, null);
    }

    private boolean requiresObject(String command) {
        return command.equals("add") || command.equals("add_if_max") ||
                command.equals("update") || command.equals("remove_lower") ||
                command.equals("insert_at");
    }

    private MusicBand createMusicBand(BufferedReader scriptReader) throws IOException {
        try {
            System.out.println("Введите данные музыкальной группы:");
            String name = readField("Имя: ", scriptReader, input -> {
                if (input.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым.");
                if (input.length() > MAX_NAME_LENGTH) {
                    throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: " + MAX_NAME_LENGTH + " символов.");
                }
                return input;
            });
            if (name == null) return null; // Прерывание при EOF

            Double x = readField("Координата X: ", scriptReader, input -> {
                double value = Double.parseDouble(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата X слишком большая.");
                return value;
            });
            if (x == null) return null; // Прерывание при EOF

            Integer y = readField("Координата Y: ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата Y слишком большая.");
                return value;
            });
            if (y == null) return null; // Прерывание при EOF

            Integer participants = readField("Количество участников: ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (value < 0) throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                if (value > 1_000_000) throw new IllegalArgumentException("Количество участников слишком большое.");
                return value;
            });
            if (participants == null) return null; // Прерывание при EOF

            String description = readField("Описание (опционально): ", scriptReader, input -> input.isEmpty() ? null : input);
            // Пустой ввод или null для description не прерывает процесс, так как поле опционально

            Date establishmentDate = readField("Дата основания (гггг-мм-дд): ", scriptReader, input ->
                    new SimpleDateFormat("yyyy-MM-dd").parse(input));
            if (establishmentDate == null) return null; // Прерывание при EOF

            MusicGenre genre = readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", scriptReader, input ->
                    MusicGenre.valueOf(input.toUpperCase()));
            if (genre == null) return null; // Прерывание при EOF

            String albumName = readField("Название лучшего альбома: ", scriptReader, input -> {
                if (input.isEmpty()) throw new IllegalArgumentException("Название альбома не может быть пустым.");
                return input;
            });
            if (albumName == null) return null; // Прерывание при EOF

            Integer albumLength = readField("Длина альбома (в секундах): ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (value <= 0) throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                if (value > 1_000_000) throw new IllegalArgumentException("Длина альбома слишком большая.");
                return value;
            });
            if (albumLength == null) return null; // Прерывание при EOF

            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(albumName, albumLength);
            return new MusicBand(name, coordinates, participants, description, establishmentDate, genre, bestAlbum);
        } catch (Exception e) {
            System.out.println("Ошибка создания музыкальной группы: " + e.getMessage());
            if (scriptReader != null) throw new IOException("Некорректные данные в скрипте: " + e.getMessage());
            return null;
        }
    }

    private <T> T readField(String prompt, BufferedReader scriptReader, Validator<T> validator) throws IOException {
        while (true) {
            try {
                String input = readLine(prompt, scriptReader);
                if (input == null && scriptReader == null) {
                    System.out.println("Ввод прерван (Ctrl+D).");
                    return null; // Прерывание при EOF
                }
                return validator.validate(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                if (scriptReader != null) throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка ввода: " + e.getMessage());
                if (scriptReader != null) throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
            }
        }
    }

    private String readLine(String prompt, BufferedReader scriptReader) throws IOException {
        System.out.print(prompt);
        if (scriptReader != null) {
            String line = scriptReader.readLine();
            if (line == null) throw new IOException("Достигнут конец файла скрипта.");
            System.out.println(line);
            return line.trim();
        } else {
            if (!scanner.hasNextLine()) {
                return null; // Возвращаем null при EOF (Ctrl+D)
            }
            return scanner.nextLine().trim();
        }
    }

    @FunctionalInterface
    private interface Validator<T> {
        T validate(String input) throws Exception;
    }
}