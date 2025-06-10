package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class AddCommand implements Command {
    private static final Logger logger = LogManager.getLogger(AddCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public AddCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (!(object instanceof MusicBand)) {
            logger.error("Invalid object type for add command");
            throw new IllegalArgumentException("Command requires a MusicBand object");
        }

        MusicBand band = (MusicBand) object;
        try {
            // Note: userId will be set by the CommandProcessor
            collection.add(band);
            logger.info("Added band {} to collection", band.getName());
        } catch (Exception e) {
            logger.error("Error adding band {}: {}", band.getName(), e.getMessage());
            throw new RuntimeException("Error adding band: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "add a new music band to the collection";
    }
}