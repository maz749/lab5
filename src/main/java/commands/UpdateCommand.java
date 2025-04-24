package commands;

import manager.MusicBandManager;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateCommand implements Command {
    private MusicBandManager manager;

    public UpdateCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String argument) {
        execute(argument, null);
    }

    @Override
    public void execute(String argument, BufferedReader reader) {
        if (argument == null || argument.trim().isEmpty()) {
            System.out.println("Ошибка: Укажите ID музыкальной группы для обновления.");
            return;
        }

        try {
            int id = Integer.parseInt(argument.trim());
            MusicBand existingBand = manager.getMusicBandById(id);
            if (existingBand == null) {
                System.out.println("Музыкальная группа с ID " + id + " не найдена.");
                return;
            }

            System.out.println("Обновление данных для музыкальной группы с ID " + id + ":");

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
            MusicBand updatedBand = new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);

            manager.update(id, updatedBand);
            System.out.println("Музыкальная группа с ID " + id + " успешно обновлена: " + updatedBand);
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении музыкальной группы: " + e.getMessage());
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