package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Команда для удаления группы по ID.
 */
public class RemoveByIdCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RemoveByIdCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public RemoveByIdCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("ID required for remove_by_id command");
            throw new IllegalArgumentException("ID required for remove_by_id command");
        }

        try {
            int id = Integer.parseInt(argument);
            // Note: userId will be set by the CommandProcessor
            collection.removeById(id);
            logger.info("Removed band with id {} from collection", id);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format: {}", argument);
            throw new IllegalArgumentException("ID must be a number");
        } catch (Exception e) {
            logger.error("Error removing band: {}", e.getMessage());
            throw new RuntimeException("Error removing band: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "remove a music band by its ID";
    }
}