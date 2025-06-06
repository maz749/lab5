//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.manager;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lab6.lab.common.models.Album;
import lab6.lab.common.models.Coordinates;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.models.MusicGenre;

public class MusicBandFactory {
    public MusicBandFactory() {
    }

    public MusicBand parseMusicBand(String line) {
        line = line.trim().replaceAll("\\s+", " ");
        if (line.isEmpty()) {
            return null;
        } else {
            String[] parts = line.split(",", -1);
            if (parts.length != 9) {
                System.out.println("Некорректная строка (неверное количество полей): " + line);
                return null;
            } else {
                String name = parts[0].trim();
                String description = parts[4].trim();
                String bestAlbumName = parts[7].trim();

                Double x;
                Integer y;
                int numberOfParticipants;
                Date establishmentDate;
                MusicGenre genre;
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

                    establishmentDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(parts[5].trim());
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
                } catch (ParseException | IllegalArgumentException var15) {
                    System.out.println("Ошибка парсинга данных в строке: " + line);
                    return null;
                }

                try {
                    Coordinates coordinates = new Coordinates(x, y);
                    Album bestAlbum = new Album(bestAlbumName, bestAlbumLength);
                    if (description.isEmpty()) {
                        description = null;
                    }

                    return new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);
                } catch (IllegalArgumentException var14) {
                    IllegalArgumentException e = var14;
                    PrintStream var10000 = System.out;
                    String var10001 = e.getMessage();
                    var10000.println("Ошибка создания MusicBand: " + var10001 + " в строке: " + line);
                    return null;
                }
            }
        }
    }

    public String bandToString(MusicBand band) {
        return String.join(",", band.getName(), String.valueOf(band.getCoordinates().getX()), String.valueOf(band.getCoordinates().getY()), String.valueOf(band.getNumberOfParticipants()), band.getDescription() != null ? band.getDescription() : "", (new SimpleDateFormat("yyyy-MM-dd")).format(band.getEstablishmentDate()), band.getGenre().name(), band.getBestAlbum().getName(), String.valueOf(band.getBestAlbum().getLength()));
    }
}
