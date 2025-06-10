package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ClearCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public ClearCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        try {
            // Note: userId will be set by the CommandProcessor
            collection.clear();
            logger.info("Collection cleared");
        } catch (Exception e) {
            logger.error("Error clearing collection: {}", e.getMessage());
            throw new RuntimeException("Error clearing collection: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "clear the collection";
    }
}