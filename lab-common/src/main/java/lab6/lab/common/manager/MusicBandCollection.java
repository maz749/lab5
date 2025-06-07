package lab6.lab.common.manager;

import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MusicBandCollection {
    private static final Logger logger = LogManager.getLogger(MusicBandCollection.class);
    private final List<MusicBand> musicBands;
    private final LocalDateTime creationDate;

    public MusicBandCollection() {
        this.musicBands = Collections.synchronizedList(new ArrayList<>());
        this.creationDate = LocalDateTime.now();
        logger.info("MusicBandCollection initialized at {}", creationDate);
    }

    public List<MusicBand> getMusicBands() {
        synchronized (musicBands) {
            logger.debug("Accessing music bands, size: {}", musicBands.size());
            if (musicBands.isEmpty()) {
                logger.warn("MusicBand collection is empty when accessed.");
            } else {
                musicBands.forEach(band -> logger.debug("Band in collection: id={}, name={}, user_id={}",
                        band.getId(), band.getName(), band.getUserId()));
            }
            List<MusicBand> result = musicBands.stream()
                    .sorted()
                    .collect(Collectors.toList());
            logger.debug("Returning {} bands from getMusicBands", result.size());
            return result;
        }
    }

    public LocalDateTime getCreationDate() {
        logger.debug("Returning creation date: {}", creationDate);
        return creationDate;
    }

    public void add(MusicBand band) {
        if (band == null) {
            logger.error("Attempted to add null MusicBand to collection.");
            return;
        }
        synchronized (musicBands) {
            int initialSize = musicBands.size();
            musicBands.add(band);
            if (musicBands.size() == initialSize + 1) {
                logger.info("Successfully added band {} to collection, new size: {}", band.getName(), musicBands.size());
            } else {
                logger.error("Failed to add band {} to collection, size unchanged: {}", band.getName(), musicBands.size());
            }
        }
    }

    public boolean addIfMax(MusicBand newBand) {
        if (newBand == null) {
            logger.error("Attempted to add null MusicBand for addIfMax.");
            return false;
        }
        synchronized (musicBands) {
            Optional<MusicBand> maxBand = musicBands.stream()
                    .max(Comparator.comparing(MusicBand::getNumberOfParticipants));
            if (maxBand.isEmpty() || newBand.getNumberOfParticipants() > maxBand.get().getNumberOfParticipants()) {
                musicBands.add(newBand);
                logger.info("Added band {} as max, new size: {}", newBand.getName(), musicBands.size());
                return true;
            }
            logger.info("Band {} not added (not max), max participants: {}",
                    newBand.getName(), maxBand.get().getNumberOfParticipants());
            return false;
        }
    }

    public boolean removeById(int id) {
        synchronized (musicBands) {
            int initialSize = musicBands.size();
            boolean removed = musicBands.removeIf(band -> band.getId() == id);
            if (removed) {
                logger.info("Removed band with id {}, new size: {}", id, musicBands.size());
            } else {
                logger.warn("No band found with id {}, size: {}", id, musicBands.size());
            }
            return removed;
        }
    }

    public void clear() {
        synchronized (musicBands) {
            musicBands.clear();
            logger.info("MusicBand collection cleared, size: {}", musicBands.size());
        }
    }

    public MusicBand getMusicBandById(int id) {
        synchronized (musicBands) {
            MusicBand band = musicBands.stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (band != null) {
                logger.debug("Found band with id {}: name={}", id, band.getName());
            } else {
                logger.warn("No band found with id {}", id);
            }
            return band;
        }
    }

    public List<MusicBand> filterByNumberOfParticipants(int numberOfParticipants) {
        synchronized (musicBands) {
            List<MusicBand> filtered = musicBands.stream()
                    .filter(band -> band.getNumberOfParticipants() == numberOfParticipants)
                    .sorted()
                    .collect(Collectors.toList());
            logger.info("Filtered {} bands by number of participants: {}", filtered.size(), numberOfParticipants);
            if (!filtered.isEmpty()) {
                filtered.forEach(band -> logger.debug("Filtered band: id={}, name={}, user_id={}",
                        band.getId(), band.getName(), band.getUserId()));
            }
            return filtered;
        }
    }

    public boolean removeAnyByDescription(String description) {
        synchronized (musicBands) {
            int initialSize = musicBands.size();
            boolean removed = musicBands.removeIf(band ->
                    band.getDescription() != null && band.getDescription().equalsIgnoreCase(description));
            if (removed) {
                logger.info("Removed band with description '{}', new size: {}", description, musicBands.size());
            } else {
                logger.warn("No band found with description '{}', size: {}", description, musicBands.size());
            }
            return removed;
        }
    }

    public MusicBand getMaxByName() {
        synchronized (musicBands) {
            MusicBand maxBand = musicBands.stream()
                    .max(Comparator.comparing(MusicBand::getName))
                    .orElse(null);
            if (maxBand != null) {
                logger.debug("Max band by name: id={}, name={}", maxBand.getId(), maxBand.getName());
            } else {
                logger.warn("Collection is empty, no max band by name.");
            }
            return maxBand;
        }
    }

    public void maxByName() {
        MusicBand maxBand = getMaxByName();
        if (maxBand != null) {
            logger.info("Displaying max band by name: {}", maxBand);
            System.out.println("Группа с максимальным именем: " + maxBand);
        } else {
            logger.warn("Collection is empty for max_by_name command.");
            System.out.println("Коллекция пуста.");
        }
    }

    public MusicBand removeHead() {
        synchronized (musicBands) {
            if (musicBands.isEmpty()) {
                logger.warn("Collection is empty, cannot remove head.");
                return null;
            }
            MusicBand head = musicBands.remove(0);
            logger.info("Removed head band: id={}, name={}, new size: {}",
                    head.getId(), head.getName(), musicBands.size());
            return head;
        }
    }

    public void removeLower(MusicBand lowerBand) {
        if (lowerBand == null) {
            logger.error("Attempted to remove lower with null MusicBand.");
            return;
        }
        synchronized (musicBands) {
            int initialSize = musicBands.size();
            musicBands.removeIf(band ->
                    band.getNumberOfParticipants() < lowerBand.getNumberOfParticipants());
            if (musicBands.size() < initialSize) {
                logger.info("Removed {} bands with fewer participants than {}, new size: {}",
                        initialSize - musicBands.size(), lowerBand.getNumberOfParticipants(), musicBands.size());
            } else {
                logger.info("No bands removed with fewer participants than {}, size: {}",
                        lowerBand.getNumberOfParticipants(), musicBands.size());
            }
        }
    }

    public void update(int id, MusicBand updatedBand) {
        if (updatedBand == null) {
            logger.error("Attempted to update with null MusicBand.");
            return;
        }
        synchronized (musicBands) {
            for (int i = 0; i < musicBands.size(); i++) {
                if (musicBands.get(i).getId() == id) {
                    updatedBand.setId(id);
                    musicBands.set(i, updatedBand);
                    logger.info("Updated band with id {}, name={}", id, updatedBand.getName());
                    return;
                }
            }
            logger.warn("Band with id {} not found for update.", id);
            System.out.println("Группа с ID " + id + " не найдена.");
        }
    }

    public void sort() {
        synchronized (musicBands) {
            musicBands.sort(Comparator.naturalOrder());
            logger.info("Collection sorted, size: {}", musicBands.size());
        }
    }

    public double getAverageNumberOfParticipants() {
        synchronized (musicBands) {
            double average = musicBands.stream()
                    .mapToInt(MusicBand::getNumberOfParticipants)
                    .average()
                    .orElse(0);
            logger.info("Calculated average number of participants: {}", average);
            return average;
        }
    }

    public int size() {
        synchronized (musicBands) {
            int size = musicBands.size();
            logger.debug("Collection size requested: {}", size);
            return size;
        }
    }
}