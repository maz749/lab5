package commands;

import manager.MusicBandManager;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Команда для добавления новой музыкальной группы в коллекцию.
 */
public class AddCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды AddCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public AddCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду добавления новой музыкальной группы.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        execute(argument, null);
    }

    /**
     * Выполняет команду добавления новой музыкальной группы, используя указанный BufferedReader.
     *
     * @param argument аргумент команды (не используется)
     * @param reader источник ввода данных
     */
    @Override
    public void execute(String argument, BufferedReader reader) {
        try {
            System.out.println("Введите данные для новой музыкальной группы:");

            String name = readLine("Имя группы: ", reader);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустым.");
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
            if (numberOfParticipants < 0) {
                throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
            }

            String description = readLine("Описание: ", reader);

            String dateStr = readLine("Дата основания (гггг-мм-дд): ", reader);
            if (dateStr.isEmpty()) {
                throw new IllegalArgumentException("Дата основания не может быть пустой.");
            }
            Date establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

            String genreInput = readLine("Жанр (ROCK, PSYCHEDELIC_CLOUD_RAP, POP, POST_PUNK): ", reader);
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
            MusicBand band = new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);

            manager.getMusicBands().add(band);
            System.out.println("Музыкальная группа добавлена: " + band);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении музыкальной группы: " + e.getMessage());
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
            return new java.util.Scanner(System.in).nextLine().trim();
        }
    }
}