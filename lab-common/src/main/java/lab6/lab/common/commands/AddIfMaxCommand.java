package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Команда для добавления группы, если её количество участников больше максимального в коллекции.
 */
public class AddIfMaxCommand implements Command {
    private static final Logger logger = LogManager.getLogger(AddIfMaxCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public AddIfMaxCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (!(object instanceof MusicBand)) {
            logger.error("Invalid object type for add_if_max command");
            throw new IllegalArgumentException("Command requires a MusicBand object");
        }

        MusicBand band = (MusicBand) object;
        try {
            // Note: userId will be set by the CommandProcessor
            boolean added = collection.addIfMax(band);
            if (added) {
                logger.info("Added band {} as max to collection", band.getName());
            } else {
                logger.info("Band {} not added as it is not max", band.getName());
            }
        } catch (Exception e) {
            logger.error("Error in add_if_max command: {}", e.getMessage());
            throw new RuntimeException("Error in add_if_max command: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "add a new music band if it has the maximum number of participants";
    }
}