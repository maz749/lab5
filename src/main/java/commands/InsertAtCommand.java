package commands;

import manager.MusicBandCollection;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class InsertAtCommand implements Command {
    private MusicBandCollection collection;

    public InsertAtCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        execute(argument, null);
    }

    @Override
    public void execute(String argument, BufferedReader reader) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указан индекс.");
            }
            int index = Integer.parseInt(argument);
            if (index < 0 || index > collection.getMusicBands().size()) {
                throw new IllegalArgumentException("Индекс вне допустимого диапазона.");
            }

            MusicBand newBand = createMusicBand(reader);
            if (newBand != null) {
                collection.getMusicBands().add(index, newBand);
                System.out.println("Группа добавлена на позицию " + index + ": " + newBand);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Индекс должен быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private MusicBand createMusicBand(BufferedReader reader) {
        while (true) {
            try {
                System.out.println("Введите данные для новой музыкальной группы:");

                String name = readLine("Имя группы: ", reader);
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Имя группы не может быть пустым.");
                }

                String xInput = readLine("Координата X: ", reader);
                if (xInput.isEmpty()) {
                    throw new IllegalArgumentException("Координата X не может быть пустой.");
                }
                double x = Double.parseDouble(xInput);

                String yInput = readLine("Координата Y: ", reader);
                if (yInput.isEmpty()) {
                    throw new IllegalArgumentException("Координата Y не может быть пустой.");
                }
                int y = Integer.parseInt(yInput);

                String participantsInput = readLine("Количество участников: ", reader);
                if (participantsInput.isEmpty()) {
                    throw new IllegalArgumentException("Количество участников не может быть пустым.");
                }
                int numberOfParticipants = Integer.parseInt(participantsInput);
                if (numberOfParticipants <= 0) {
                    throw new IllegalArgumentException("Количество участников должно быть больше 0.");
                }

                String description = readLine("Описание (можно оставить пустым): ", reader);
                if (description.isEmpty()) {
                    description = null;
                }

                String dateStr = readLine("Дата основания (yyyy-MM-dd): ", reader);
                if (dateStr.isEmpty()) {
                    throw new IllegalArgumentException("Дата основания не может быть пустой.");
                }
                Date establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

                String genreInput = readLine("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", reader);
                if (genreInput.isEmpty()) {
                    throw new IllegalArgumentException("Жанр не может быть пустым.");
                }
                MusicGenre genre = MusicGenre.valueOf(genreInput.toUpperCase());

                String albumName = readLine("Название лучшего альбома: ", reader);
                if (albumName.isEmpty()) {
                    throw new IllegalArgumentException("Название альбома не может быть пустым.");
                }

                String albumLengthInput = readLine("Длина альбома (в секундах): ", reader);
                if (albumLengthInput.isEmpty()) {
                    throw new IllegalArgumentException("Длина альбома не может быть пустой.");
                }
                int albumLength = Integer.parseInt(albumLengthInput);
                if (albumLength <= 0) {
                    throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                }

                Coordinates coordinates = new Coordinates(x, y);
                Album bestAlbum = new Album(albumName, albumLength);
                return new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);
            } catch (Exception e) {
                System.out.println("Ошибка при создании музыкальной группы: " + e.getMessage());
                if (reader == null) {
                    System.out.println("Попробуйте снова.");
                } else {
                    return null; // В скрипте не повторяем ввод
                }
            }
        }
    }

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
}