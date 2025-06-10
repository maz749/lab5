package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Команда для вставки группы на указанную позицию в коллекции.
 */
public class InsertAtCommand implements Command {
    private static final Logger logger = LogManager.getLogger(InsertAtCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public InsertAtCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("Index required for insert_at command");
            throw new IllegalArgumentException("Index required for insert_at command");
        }

        if (!(object instanceof MusicBand)) {
            logger.error("Invalid object type for insert_at command");
            throw new IllegalArgumentException("Command requires a MusicBand object");
        }

        try {
            int index = Integer.parseInt(argument);
            MusicBand band = (MusicBand) object;
            // Note: userId will be set by the CommandProcessor
            synchronized (collection.getMusicBands()) {
                List<MusicBand> bands = collection.getMusicBands();
                bands.removeIf(b -> b.getId() == band.getId());
                if (index >= 0 && index <= bands.size()) {
                    bands.add(index, band);
                    logger.info("Inserted band {} at index {}", band.getName(), index);
                } else {
                    bands.add(band);
                    logger.info("Index {} out of bounds, added band {} at the end", index, band.getName());
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid index format: {}", argument);
            throw new IllegalArgumentException("Index must be a number");
        } catch (Exception e) {
            logger.error("Error in insert_at command: {}", e.getMessage());
            throw new RuntimeException("Error in insert_at command: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "insert a music band at the specified index";
    }
}