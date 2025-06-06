//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 1L;
    private static long nextId = 1L;
    private static final int MAX_NAME_LENGTH = 1000;
    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private int numberOfParticipants;
    private String description;
    private Date establishmentDate;
    private MusicGenre genre;
    private Album bestAlbum;

    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants, String description, Date establishmentDate, MusicGenre genre, Album bestAlbum) {
        if (name != null && !name.isEmpty()) {
            if (name.length() > 1000) {
                throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: 1000 символов.");
            } else if (coordinates == null) {
                throw new IllegalArgumentException("Координаты не могут быть null.");
            } else if (Math.abs(coordinates.getX()) > 1000000.0) {
                throw new IllegalArgumentException("Координата X слишком большая.");
            } else if (Math.abs(coordinates.getY()) > 1000000) {
                throw new IllegalArgumentException("Координата Y слишком большая.");
            } else if (numberOfParticipants < 0) {
                throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
            } else if (numberOfParticipants > 1000000) {
                throw new IllegalArgumentException("Количество участников слишком большое.");
            } else if (genre == null) {
                throw new IllegalArgumentException("Жанр не может быть null.");
            } else if (bestAlbum != null && bestAlbum.getLength() > 0) {
                if (bestAlbum.getLength() > 1000000) {
                    throw new IllegalArgumentException("Длина альбома слишком большая.");
                } else if (establishmentDate == null) {
                    throw new IllegalArgumentException("Дата основания не может быть null.");
                } else {
                    this.id = (long)(nextId++);
                    this.name = name;
                    this.coordinates = coordinates;
                    this.creationDate = LocalDateTime.now();
                    this.numberOfParticipants = numberOfParticipants;
                    this.description = description;
                    this.establishmentDate = establishmentDate;
                    this.genre = genre;
                    this.bestAlbum = bestAlbum;
                }
            } else {
                throw new IllegalArgumentException("Альбом должен быть корректным с длиной больше 0.");
            }
        } else {
            throw new IllegalArgumentException("Имя не может быть пустым или null.");
        }
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public int getNumberOfParticipants() {
        return this.numberOfParticipants;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getEstablishmentDate() {
        return this.establishmentDate;
    }

    public MusicGenre getGenre() {
        return this.genre;
    }

    public Album getBestAlbum() {
        return this.bestAlbum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public void setBestAlbum(Album bestAlbum) {
        this.bestAlbum = bestAlbum;
    }

    public int compareTo(MusicBand other) {
        return this.name.compareTo(other.name);
    }

    public String toString() {
        long var10000 = this.id;
        return "MusicBand{id=" + var10000 + ", name='" + this.name + "', coordinates=" + String.valueOf(this.coordinates) + ", creationDate=" + String.valueOf(this.creationDate) + ", numberOfParticipants=" + this.numberOfParticipants + ", description='" + this.description + "', establishmentDate=" + String.valueOf(this.establishmentDate != null ? this.establishmentDate : "null") + ", genre=" + String.valueOf(this.genre) + ", bestAlbum=" + String.valueOf(this.bestAlbum) + "}";
    }
}
