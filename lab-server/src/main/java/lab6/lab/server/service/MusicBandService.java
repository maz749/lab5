package lab6.lab.server.service;

import lab6.lab.common.models.MusicBand;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.manager.MusicBandCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MusicBandService {
    private static final Logger logger = LogManager.getLogger(MusicBandService.class);
    private final MusicBandRepository repository;
    private final MusicBandCollection collection;
    private final ReadWriteLock collectionLock;

    public MusicBandService(MusicBandRepository repository, MusicBandCollection collection) {
        this.repository = repository;
        this.collection = collection;
        this.collectionLock = new ReentrantReadWriteLock();
    }

    public List<MusicBand> getAllBands() throws SQLException {
        collectionLock.readLock().lock();
        try {
            return collection.getMusicBands();
        } finally {
            collectionLock.readLock().unlock();
        }
    }

    public List<MusicBand> getBandsByUserId(Long userId) throws SQLException {
        collectionLock.readLock().lock();
        try {
            return collection.getMusicBands().stream()
                    .filter(band -> band.getUserId() == userId)
                    .toList();
        } finally {
            collectionLock.readLock().unlock();
        }
    }

    public boolean canModifyBand(MusicBand band, Long userId) {
        if (band == null || userId == null) {
            return false;
        }
        // New bands can be created by any authenticated user
        if (band.getId() == 0) {
            return true;
        }
        // Existing bands can only be modified by their owner
        return band.getUserId() == userId;
    }

    public void addBand(MusicBand band, Long userId) throws SQLException {
        if (!canModifyBand(band, userId)) {
            throw new SQLException("Permission denied: cannot add band");
        }

        collectionLock.writeLock().lock();
        try {
            repository.addBand(band, userId);
            collection.add(band);
            logger.info("Added new band: id={}, name={}, userId={}", band.getId(), band.getName(), userId);
        } finally {
            collectionLock.writeLock().unlock();
        }
    }

    public void updateBand(int id, MusicBand band, Long userId) throws SQLException {
        collectionLock.writeLock().lock();
        try {
            MusicBand existingBand = collection.getMusicBands().stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (existingBand == null) {
                throw new SQLException("Band not found: " + id);
            }

            if (!canModifyBand(existingBand, userId)) {
                throw new SQLException("Permission denied: cannot modify band " + id);
            }

            repository.updateBand(id, band, userId);
            collection.update(id, band);
            logger.info("Updated band: id={}, name={}, userId={}", id, band.getName(), userId);
        } finally {
            collectionLock.writeLock().unlock();
        }
    }

    public void removeBand(int id, Long userId) throws SQLException {
        collectionLock.writeLock().lock();
        try {
            MusicBand existingBand = collection.getMusicBands().stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (existingBand == null) {
                throw new SQLException("Band not found: " + id);
            }

            if (!canModifyBand(existingBand, userId)) {
                throw new SQLException("Permission denied: cannot remove band " + id);
            }

            repository.deleteBand(id, userId);
            collection.removeById(id);
            logger.info("Removed band: id={}, userId={}", id, userId);
        } finally {
            collectionLock.writeLock().unlock();
        }
    }

    public void clearUserBands(Long userId) throws SQLException {
        collectionLock.writeLock().lock();
        try {
            repository.deleteBandsByUserId(userId);
            collection.getMusicBands().removeIf(band -> band.getUserId() == userId);
            logger.info("Cleared all bands for userId: {}", userId);
        } finally {
            collectionLock.writeLock().unlock();
        }
    }

    public void removeLowerBands(MusicBand band, Long userId) throws SQLException {
        if (!canModifyBand(band, userId)) {
            throw new SQLException("Permission denied: cannot remove bands");
        }

        collectionLock.writeLock().lock();
        try {
            repository.deleteLowerBands(band.getNumberOfParticipants(), userId);
            collection.getMusicBands().removeIf(b ->
                    b.getUserId() == userId &&
                            b.getNumberOfParticipants() < band.getNumberOfParticipants()
            );
            logger.info("Removed lower bands for userId: {}", userId);
        } finally {
            collectionLock.writeLock().unlock();
        }
    }

    public void loadCollection() throws SQLException {
        collectionLock.writeLock().lock();
        try {
            collection.clear();
            List<MusicBand> bands = repository.getAllBands();
            for (MusicBand band : bands) {
                collection.add(band);
            }
            logger.info("Loaded {} bands into collection", bands.size());
        } finally {
            collectionLock.writeLock().unlock();
        }
    }
}