package lab6.lab.common.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private int numberOfParticipants;
    private String description;
    private Date establishmentDate;
    private MusicGenre genre;
    private Album bestAlbum;
    private long userId;

    public MusicBand() {
    }

    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants,
                     String description, Date establishmentDate, MusicGenre genre, Album bestAlbum) {
        this.name = name;
        this.coordinates = coordinates;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
        this.creationDate = LocalDateTime.now();
    }

    public MusicBand(long id, String name, Coordinates coordinates, LocalDateTime creationDate,
                     int numberOfParticipants, String description, Date establishmentDate,
                     MusicGenre genre, Album bestAlbum, long userId) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
        this.userId = userId;
    }


    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants,
                     String description, Date establishmentDate, MusicGenre genre, Album bestAlbum, long userId) {
        this.name = name;
        this.coordinates = coordinates;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
        this.userId = userId;
        this.creationDate = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() {
        return (int) id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public Album getBestAlbum() {
        return bestAlbum;
    }

    public void setBestAlbum(Album bestAlbum) {
        this.bestAlbum = bestAlbum;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(MusicBand other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return String.format(
                "MusicBand{id=%d, name='%s', coordinates=%s, creationDate=%s, numberOfParticipants=%d, " +
                        "description='%s', establishmentDate=%s, genre=%s, bestAlbum=%s, userId=%d}",
                id, name, coordinates, creationDate, numberOfParticipants,
                description != null ? description : "null",
                establishmentDate != null ? establishmentDate : "null",
                genre, bestAlbum, userId
        );
    }
}