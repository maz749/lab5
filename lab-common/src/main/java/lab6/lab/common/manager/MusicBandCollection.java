//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lab6.lab.common.models.MusicBand;

public class MusicBandCollection {
    private final List<MusicBand> musicBands = new ArrayList();

    public MusicBandCollection() {
    }

    public List<MusicBand> getMusicBands() {
        return (List)this.musicBands.stream().sorted().collect(Collectors.toList());
    }

    public void add(MusicBand band) {
        this.musicBands.add(band);
    }

    public boolean addIfMax(MusicBand newBand) {
        Optional<MusicBand> maxBand = this.musicBands.stream().max(Comparator.comparing(MusicBand::getNumberOfParticipants));
        if (!maxBand.isEmpty() && newBand.getNumberOfParticipants() <= ((MusicBand)maxBand.get()).getNumberOfParticipants()) {
            System.out.println("Музыкальная группа не добавлена, так как она не максимальна");
            return false;
        } else {
            this.musicBands.add(newBand);
            System.out.println("Музыкальная группа добавлена: " + String.valueOf(newBand));
            return true;
        }
    }

    public boolean removeById(int id) {
        return this.musicBands.removeIf((band) -> {
            return band.getId() == (long)id;
        });
    }

    public void clear() {
        this.musicBands.clear();
    }

    public MusicBand getMusicBandById(int id) {
        return (MusicBand)this.musicBands.stream().filter((band) -> {
            return band.getId() == (long)id;
        }).findFirst().orElse((MusicBand) null);
    }

    public List<MusicBand> filterByNumberOfParticipants(int numberOfParticipants) {
        return (List)this.musicBands.stream().filter((band) -> {
            return band.getNumberOfParticipants() == numberOfParticipants;
        }).sorted().collect(Collectors.toList());
    }

    public boolean removeAnyByDescription(String description) {
        return this.musicBands.removeIf((band) -> {
            return band.getDescription() != null && band.getDescription().equalsIgnoreCase(description);
        });
    }

    public MusicBand getMaxByName() {
        return (MusicBand)this.musicBands.stream().max(Comparator.comparing(MusicBand::getName)).orElse((MusicBand) null);
    }

    public void maxByName() {
        MusicBand maxBand = this.getMaxByName();
        if (maxBand != null) {
            System.out.println("Группа с максимальным именем: " + String.valueOf(maxBand));
        } else {
            System.out.println("Коллекция пуста.");
        }

    }

    public MusicBand removeHead() {
        return this.musicBands.isEmpty() ? null : (MusicBand)this.musicBands.remove(0);
    }

    public void removeLower(MusicBand lowerBand) {
        this.musicBands.removeIf((band) -> {
            return band.getNumberOfParticipants() < lowerBand.getNumberOfParticipants();
        });
    }

    public void update(int id, MusicBand updatedBand) {
        for(int i = 0; i < this.musicBands.size(); ++i) {
            if (((MusicBand)this.musicBands.get(i)).getId() == (long)id) {
                updatedBand.setId((long)id);
                this.musicBands.set(i, updatedBand);
                return;
            }
        }

        System.out.println("Группа с ID " + id + " не найдена.");
    }

    public void sort() {
        this.musicBands.sort(Comparator.naturalOrder());
    }

    public double getAverageNumberOfParticipants() {
        return this.musicBands.stream().mapToInt(MusicBand::getNumberOfParticipants).average().orElse(0.0);
    }
}
