package commands;

import manager.MusicBandCollection;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 * Команда для добавления новой музыкальной группы в коллекцию.
 */
public class AddCommand implements Command {
    private MusicBandCollection collection;

    public AddCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        execute(argument, null);
    }

    @Override
    public void execute(String argument, BufferedReader reader) {
        System.out.println("Введите данные для новой музыкальной группы:");

        try {
            // Ввод имени группы
            String name = readField("Имя группы: ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Имя не может быть пустым.");
                }
                return input;
            });

            // Ввод координаты X
            double x = readField("Координата X: ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Координата X не может быть пустой.");
                }
                try {
                    return Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Координата X должна быть числом.");
                }
            });

            // Ввод координаты Y
            int y = readField("Координата Y: ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Координата Y не может быть пустой.");
                }
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Координата Y должна быть целым числом.");
                }
            });

            // Ввод количества участников
            int numberOfParticipants = readField("Количество участников: ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Количество участников не может быть пустым.");
                }
                try {
                    int value = Integer.parseInt(input);
                    if (value < 0) {
                        throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                    }
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Количество участников должно быть целым числом.");
                }
            });

            // Ввод описания
            String description = readField("Описание (можно оставить пустым): ", reader, input -> input.isEmpty() ? null : input);

            // Ввод даты основания
            Date establishmentDate = readField("Дата основания (гггг-мм-дд): ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Дата основания не может быть пустой.");
                }
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(input);
                } catch (java.text.ParseException e) {
                    throw new IllegalArgumentException("Некорректный формат даты. Используйте гггг-мм-дд.");
                }
            });

            // Ввод жанра
            MusicGenre genre = readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Жанр не может быть пустым.");
                }
                try {
                    return MusicGenre.valueOf(input.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Некорректный жанр. Доступные жанры: " + Arrays.toString(MusicGenre.values()));
                }
            });

            // Ввод названия альбома
            String albumName = readField("Название лучшего альбома: ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Название альбома не может быть пустым.");
                }
                return input;
            });

            // Ввод длины альбома
            int albumLength = readField("Длина альбома (в секундах): ", reader, input -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Длина альбома не может быть пустой.");
                }
                try {
                    int value = Integer.parseInt(input);
                    if (value <= 0) {
                        throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                    }
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Длина альбома должна быть целым числом.");
                }
            });

            // Создание объектов и добавление группы
            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(albumName, albumLength);
            MusicBand band = new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);

            collection.add(band);
            System.out.println("Музыкальная группа добавлена: " + band);

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении музыкальной группы: " + e.getMessage());
        }
    }

    /**
     * Универсальный метод для чтения поля с повторным вводом в случае ошибки.
     *
     * @param prompt Сообщение для пользователя.
     * @param reader BufferedReader для чтения из скрипта, или null для интерактивного ввода.
     * @param validator Функция валидации и преобразования ввода.
     * @param <T> Тип возвращаемого значения.
     * @return Валидированное значение поля.
     * @throws IOException Если достигнут конец файла скрипта.
     */
    private <T> T readField(String prompt, BufferedReader reader, Validator<T> validator) throws IOException {
        while (true) {
            try {
                String input = readLine(prompt, reader);
                return validator.validate(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                if (reader != null) {
                    throw e; // В скрипте не повторяем ввод
                }
                System.out.println("Пожалуйста, введите данные заново для этого поля.");
            }
        }
    }

    /**
     * Читает строку из ввода (интерактивного или скрипта).
     *
     * @param prompt Сообщение для пользователя.
     * @param reader BufferedReader для чтения из скрипта, или null для интерактивного ввода.
     * @return Введённая строка.
     * @throws IOException Если достигнут конец файла скрипта.
     */
    private String readLine(String prompt, BufferedReader reader) throws IOException {
        System.out.print(prompt);
        if (reader != null) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Достигнут конец файла скрипта.");
            }
            System.out.println(line);
            return line.trim();
        } else {
            return new Scanner(System.in).nextLine().trim();
        }
    }

    /**
     * Интерфейс для валидации и преобразования ввода.
     *
     * @param <T> Тип возвращаемого значения.
     */
    @FunctionalInterface
    private interface Validator<T> {
        T validate(String input) throws IllegalArgumentException;
    }
}