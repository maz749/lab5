package lab6.lab.client;

import lab6.lab.common.*;
import lab6.lab.common.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class CommandParser {
    private static final Logger logger = LogManager.getLogger(CommandParser.class);
    private final Scanner scanner;
    private static final int MAX_NAME_LENGTH = 1000;

    public CommandParser() {
        this.scanner = new Scanner(System.in);
    }

    public CommandRequest parseCommand(String input, BufferedReader scriptReader, String username, String password) throws IOException {
        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        if (command.equals("login") || command.equals("register")) {
            return new CommandRequest(command, argument, null, username, password);
        }

        if (requiresObject(command)) {
            MusicBand band = scriptReader != null ? createMusicBandFromScript(scriptReader) : createMusicBand(null);
            if (band == null) return null;
            return new CommandRequest(command, argument, band, username, password);
        }

        return new CommandRequest(command, argument, null, username, password);
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
                    throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: " + MAX_NAME_LENGTH);
                }
                return input;
            });
            if (name == null) return null;

            Double x = readField("Координата X: ", scriptReader, input -> {
                double value = Double.parseDouble(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата X слишком большая.");
                return value;
            });
            if (x == null) return null;

            Integer y = readField("Координата Y: ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата Y слишком большая.");
                return value;
            });
            if (y == null) return null;

            Integer participants = readField("Количество участников: ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (value < 0) throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                if (value > 1_000_000) throw new IllegalArgumentException("Количество участников слишком большое.");
                return value;
            });
            if (participants == null) return null;

            String description = readField("Описание (опционально): ", scriptReader, input -> input.isEmpty() ? null : input);

            Date establishmentDate = readField("Дата основания (гггг-мм-дд): ", scriptReader, input ->
                    new SimpleDateFormat("yyyy-MM-dd").parse(input));
            if (establishmentDate == null) return null;

            MusicGenre genre = readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", scriptReader, input ->
                    MusicGenre.valueOf(input.toUpperCase()));
            if (genre == null) return null;

            String albumName = readField("Название лучшего альбома: ", scriptReader, input -> {
                if (input.isEmpty()) throw new IllegalArgumentException("Название альбома не может быть пустым.");
                return input;
            });
            if (albumName == null) return null;

            Integer albumLength = readField("Длина альбома (в секундах): ", scriptReader, input -> {
                int value = Integer.parseInt(input);
                if (value <= 0) throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                if (value > 1_000_000) throw new IllegalArgumentException("Длина альбома слишком большая.");
                return value;
            });
            if (albumLength == null) return null;

            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(albumName, albumLength);
            return new MusicBand(name, coordinates, participants, description, establishmentDate, genre, bestAlbum);
        } catch (Exception e) {
            logger.error("Error creating MusicBand: {}", e.getMessage());
            System.out.println("Ошибка создания музыкальной группы: " + e.getMessage());
            if (scriptReader != null) throw new IOException("Некорректные данные в скрипте: " + e.getMessage());
            return null;
        }
    }

    private MusicBand createMusicBandFromScript(BufferedReader scriptReader) throws IOException {
        try {
            String name = readField("Имя: ", scriptReader, input -> {
                logger.debug("Reading name: {}", input);
                if (input.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым.");
                if (input.length() > MAX_NAME_LENGTH) {
                    throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: " + MAX_NAME_LENGTH);
                }
                return input;
            });
            if (name == null) {
                logger.warn("Insufficient data for band name.");
                return null;
            }

            Double x = readField("Координата X: ", scriptReader, input -> {
                logger.debug("Reading X coordinate: {}", input);
                double value = Double.parseDouble(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата X слишком большая.");
                return value;
            });
            if (x == null) {
                logger.warn("Insufficient data for X coordinate.");
                return null;
            }

            Integer y = readField("Координата Y: ", scriptReader, input -> {
                logger.debug("Reading Y coordinate: {}", input);
                int value = Integer.parseInt(input);
                if (Math.abs(value) > 1_000_000) throw new IllegalArgumentException("Координата Y слишком большая.");
                return value;
            });
            if (y == null) {
                logger.warn("Insufficient data for Y coordinate.");
                return null;
            }

            Integer participants = readField("Количество участников: ", scriptReader, input -> {
                logger.debug("Reading number of participants: {}", input);
                int value = Integer.parseInt(input);
                if (value < 0) throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                if (value > 1_000_000) throw new IllegalArgumentException("Количество участников слишком большое.");
                return value;
            });
            if (participants == null) {
                logger.warn("Insufficient data for number of participants.");
                return null;
            }

            String description = readField("Описание (опционально): ", scriptReader, input -> {
                logger.debug("Reading description: {}", input);
                return input.isEmpty() ? null : input;
            });

            Date establishmentDate = readField("Дата основания (гггг-мм-дд): ", scriptReader, input -> {
                logger.debug("Reading establishment date: {}", input);
                return new SimpleDateFormat("yyyy-MM-dd").parse(input);
            });
            if (establishmentDate == null) {
                logger.warn("Insufficient data for establishment date.");
                return null;
            }

            MusicGenre genre = readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", scriptReader, input -> {
                logger.debug("Reading genre: {}", input);
                return MusicGenre.valueOf(input.toUpperCase());
            });
            if (genre == null) {
                logger.warn("Insufficient data for genre.");
                return null;
            }

            String albumName = readField("Название лучшего альбома: ", scriptReader, input -> {
                logger.debug("Reading album name: {}", input);
                if (input.isEmpty()) throw new IllegalArgumentException("Название альбома не может быть пустым.");
                return input;
            });
            if (albumName == null) {
                logger.warn("Insufficient data for album name.");
                return null;
            }

            Integer albumLength = readField("Длина альбома (в секундах): ", scriptReader, input -> {
                logger.debug("Reading album length: {}", input);
                int value = Integer.parseInt(input);
                if (value <= 0) throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                if (value > 1_000_000) throw new IllegalArgumentException("Длина альбома слишком большая.");
                return value;
            });
            if (albumLength == null) {
                logger.warn("Insufficient data for album length.");
                return null;
            }

            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(albumName, albumLength);
            MusicBand band = new MusicBand(name, coordinates, participants, description, establishmentDate, genre, bestAlbum);
            logger.info("Created MusicBand: {}", band.getName());
            return band;
        } catch (Exception e) {
            logger.error("Failed to create MusicBand from script: {}", e.getMessage());
            System.out.println("Пропущена команда из-за ошибки: " + e.getMessage());
            return null;
        }
    }

    private <T> T readField(String prompt, BufferedReader scriptReader, Validator<T> validator) throws IOException {
        while (true) {
            try {
                String input = readLine(prompt, scriptReader);
                if (input == null && scriptReader == null) {
                    logger.warn("Input interrupted (Ctrl+D).");
                    System.out.println("Ввод прерван (Ctrl+D).");
                    return null;
                }
                return validator.validate(input);
            } catch (IllegalArgumentException e) {
                logger.error("Validation error: {}", e.getMessage());
                System.out.println("Ошибка: " + e.getMessage());
                if (scriptReader != null) throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Input error: {}", e.getMessage());
                System.out.println("Ошибка ввода: " + e.getMessage());
                if (scriptReader != null) throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
            }
        }
    }

    private String readLine(String prompt, BufferedReader scriptReader) throws IOException {
        System.out.print(prompt);
        if (scriptReader != null) {
            String line = scriptReader.readLine();
            if (line == null) {
                logger.warn("Reached end of script file.");
                throw new IOException("Достигнут конец файла скрипта.");
            }
            System.out.println(line);
            return line.trim();
        } else {
            if (!scanner.hasNextLine()) {
                return null;
            }
            return scanner.nextLine().trim();
        }
    }

    @FunctionalInterface
    private interface Validator<T> {
        T validate(String input) throws Exception;
    }
}