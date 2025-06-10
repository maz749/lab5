package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SortCommand implements Command {
    private static final Logger logger = LogManager.getLogger(SortCommand.class);
    private final MusicBandCollection collection;

    public SortCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        try {
            collection.sort();
            logger.info("Collection sorted by name");
            System.out.println("Коллекция отсортирована по имени.");
        } catch (Exception e) {
            logger.error("Error sorting collection: {}", e.getMessage());
            throw new RuntimeException("Error sorting collection: " + e.getMessage(), e);
        }
    }

    public CommandResponse executeWithResponse() {
        try {
            collection.sort();
            List<MusicBand> sortedBands = collection.getMusicBands();
            logger.info("Collection sorted by name, {} bands", sortedBands.size());
            return new CommandResponse("Collection sorted by name:", sortedBands, true);
        } catch (Exception e) {
            logger.error("Error sorting collection: {}", e.getMessage());
            throw new RuntimeException("Error sorting collection: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "sort the collection by band name";
    }
}
