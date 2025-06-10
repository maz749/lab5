package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateCommand implements Command {
    private static final Logger logger = LogManager.getLogger(UpdateCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public UpdateCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("ID required for update command");
            throw new IllegalArgumentException("ID required for update command");
        }

        if (!(object instanceof MusicBand)) {
            logger.error("Invalid object type for update command");
            throw new IllegalArgumentException("Command requires a MusicBand object");
        }

        try {
            int id = Integer.parseInt(argument);
            MusicBand band = (MusicBand) object;
            // Note: userId will be set by the CommandProcessor
            collection.update(id, band);
            logger.info("Updated band with id {} in collection", id);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format: {}", argument);
            throw new IllegalArgumentException("ID must be a number");
        } catch (Exception e) {
            logger.error("Error updating band: {}", e.getMessage());
            throw new RuntimeException("Error updating band: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "update a music band by its ID";
    }
}