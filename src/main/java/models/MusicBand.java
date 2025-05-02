package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 1L;
    private static long nextId = 1;
    private static final int MAX_NAME_LENGTH = 1000; // Ограничение на длину имени

    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private int numberOfParticipants;
    private String description;
    private Date establishmentDate;
    private MusicGenre genre;
    private Album bestAlbum;

    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants, String description,
                     Date establishmentDate, MusicGenre genre, Album bestAlbum) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым или null.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: " + MAX_NAME_LENGTH + " символов.");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null.");
        }
        if (Math.abs(coordinates.getX()) > 1_000_000) {
            throw new IllegalArgumentException("Координата X слишком большая.");
        }
        if (Math.abs(coordinates.getY()) > 1_000_000) {
            throw new IllegalArgumentException("Координата Y слишком большая.");
        }
        if (numberOfParticipants < 0) {
            throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
        }
        if (numberOfParticipants > 1_000_000) {
            throw new IllegalArgumentException("Количество участников слишком большое.");
        }
        if (genre == null) {
            throw new IllegalArgumentException("Жанр не может быть null.");
        }
        if (bestAlbum == null || bestAlbum.getLength() <= 0) {
            throw new IllegalArgumentException("Альбом должен быть корректным с длиной больше 0.");
        }
        if (bestAlbum.getLength() > 1_000_000) {
            throw new IllegalArgumentException("Длина альбома слишком большая.");
        }
        if (establishmentDate == null) {
            throw new IllegalArgumentException("Дата основания не может быть null.");
        }

        this.id = nextId++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public String getDescription() {
        return description;
    }

    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Album getBestAlbum() {
        return bestAlbum;
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

    @Override
    public int compareTo(MusicBand other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return "MusicBand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", description='" + description + '\'' +
                ", establishmentDate=" + (establishmentDate != null ? establishmentDate : "null") +
                ", genre=" + genre +
                ", bestAlbum=" + bestAlbum +
                '}';
    }
}