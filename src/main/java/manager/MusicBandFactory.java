package manager;

import models.*;

import java.text.*;
import java.util.*;

/**
 * Класс для создания объектов MusicBand из строк CSV.
 */
public class MusicBandFactory {
    public MusicBand parseMusicBand(String line) {
        line = line.trim().replaceAll("\\s+", " ");
        if (line.isEmpty()) {
            return null;
        }
        String[] parts = line.split(",", -1); // -1 to include empty fields

        if (parts.length != 9) {
            System.out.println("Некорректная строка (неверное количество полей): " + line);
            return null;
        }

        String name = parts[0].trim();
        Double x;
        Integer y;
        int numberOfParticipants;
        String description = parts[4].trim();
        Date establishmentDate;
        MusicGenre genre;
        String bestAlbumName = parts[7].trim();
        Integer bestAlbumLength;

        try {
            if (name.isEmpty()) {
                System.out.println("Ошибка: Пустое имя группы в строке: " + line);
                return null;
            }
            x = NumberFormat.getInstance(Locale.US).parse(parts[1].trim()).doubleValue();
            y = Integer.parseInt(parts[2].trim());
            numberOfParticipants = Integer.parseInt(parts[3].trim());
            if (numberOfParticipants < 0) {
                System.out.println("Ошибка: Отрицательное количество участников в строке: " + line);
                return null;
            }
            if (parts[5].trim().isEmpty()) {
                System.out.println("Ошибка: Пустая дата основания в строке: " + line);
                return null;
            }
            establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[5].trim());
            if (parts[6].trim().isEmpty()) {
                System.out.println("Ошибка: Пустой жанр в строке: " + line);
                return null;
            }
            genre = MusicGenre.valueOf(parts[6].trim().toUpperCase());
            if (bestAlbumName.isEmpty()) {
                System.out.println("Ошибка: Пустое название альбома в строке: " + line);
                return null;
            }
            bestAlbumLength = Integer.parseInt(parts[8].trim());
            if (bestAlbumLength <= 0) {
                System.out.println("Ошибка: Некорректная длина альбома в строке: " + line);
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка парсинга числовых данных в строке: " + line);
            return null;
        } catch (ParseException e) {
            System.out.println("Ошибка парсинга даты в строке: " + line);
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка парсинга жанра в строке: " + line);
            return null;
        }

        try {
            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(bestAlbumName, bestAlbumLength);
            if (description.isEmpty()) {
                description = null;
            }
            return new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка создания MusicBand: " + e.getMessage() + " в строке: " + line);
            return null;
        }
    }

    public String bandToString(MusicBand band) {
        return String.join(",",
                band.getName(),
                String.valueOf(band.getCoordinates().getX()),
                String.valueOf(band.getCoordinates().getY()),
                String.valueOf(band.getNumberOfParticipants()),
                band.getDescription() != null ? band.getDescription() : "",
                new SimpleDateFormat("yyyy-MM-dd").format(band.getEstablishmentDate()),
                band.getGenre().name(),
                band.getBestAlbum().getName(),
                String.valueOf(band.getBestAlbum().getLength())
        );
    }
}