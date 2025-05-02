package manager;

import models.MusicBand;

import java.util.*;
import java.util.stream.Collectors;

public class MusicBandCollection {
    private final List<MusicBand> musicBands;

    public MusicBandCollection() {
        this.musicBands = new ArrayList<>();
    }

    public List<MusicBand> getMusicBands() {
        return musicBands.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public void add(MusicBand band) {
        musicBands.add(band);
    }

    public boolean addIfMax(MusicBand newBand) {
        Optional<MusicBand> maxBand = musicBands.stream()
                .max(Comparator.comparing(MusicBand::getNumberOfParticipants));
        if (maxBand.isEmpty() || newBand.getNumberOfParticipants() > maxBand.get().getNumberOfParticipants()) {
            musicBands.add(newBand);
            System.out.println("Музыкальная группа добавлена: " + newBand);
            return true;
        } else {
            System.out.println("Музыкальная группа не добавлена, так как она не максимальна");
            return false;
        }
    }

    public boolean removeById(int id) {
        return musicBands.removeIf(band -> band.getId() == id);
    }

    public void clear() {
        musicBands.clear();
    }

    public MusicBand getMusicBandById(int id) {
        return musicBands.stream()
                .filter(band -> band.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<MusicBand> filterByNumberOfParticipants(int numberOfParticipants) {
        return musicBands.stream()
                .filter(band -> band.getNumberOfParticipants() == numberOfParticipants)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean removeAnyByDescription(String description) {
        return musicBands.removeIf(band ->
                band.getDescription() != null && band.getDescription().equalsIgnoreCase(description));
    }

    public MusicBand getMaxByName() {
        return musicBands.stream()
                .max(Comparator.comparing(MusicBand::getName))
                .orElse(null);
    }

    public void maxByName() {
        MusicBand maxBand = getMaxByName();
        if (maxBand != null) {
            System.out.println("Группа с максимальным именем: " + maxBand);
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    public MusicBand removeHead() {
        return musicBands.isEmpty() ? null : musicBands.remove(0);
    }

    public void removeLower(MusicBand lowerBand) {
        musicBands.removeIf(band ->
                band.getNumberOfParticipants() < lowerBand.getNumberOfParticipants());
    }

    public void update(int id, MusicBand updatedBand) {
        for (int i = 0; i < musicBands.size(); i++) {
            if (musicBands.get(i).getId() == id) {
                updatedBand.setId(id);
                musicBands.set(i, updatedBand);
                return;
            }
        }
        System.out.println("Группа с ID " + id + " не найдена.");
    }

    public void sort() {
        musicBands.sort(Comparator.naturalOrder());
    }

    public double getAverageNumberOfParticipants() {
        return musicBands.stream()
                .mapToInt(MusicBand::getNumberOfParticipants)
                .average()
                .orElse(0);
    }
}