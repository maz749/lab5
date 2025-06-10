package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ShowCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowCommand.class);
    private final MusicBandCollection collection;

    public ShowCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        List<MusicBand> bands = collection.getMusicBands();
        logger.debug("Show command: retrieved {} bands", bands.size());
        bands.forEach(band -> logger.debug("Band: id={}, name={}, user_id={}", band.getId(), band.getName(), band.getUserId()));
        if (bands.isEmpty()) {
            logger.info("Collection is empty");
            System.out.println("Коллекция пуста.");
        } else {
            logger.info("Showing {} music bands", bands.size());
            bands.forEach(System.out::println);
        }
    }

    public CommandResponse executeWithResponse() {
        List<MusicBand> bands = collection.getMusicBands();
        logger.debug("Show command: retrieved {} bands", bands.size());
        bands.forEach(band -> logger.debug("Band: id={}, name={}, user_id={}", band.getId(), band.getName(), band.getUserId()));
        if (bands.isEmpty()) {
            logger.info("Collection is empty");
            return new CommandResponse("Collection is empty.", null, true);
        } else {
            logger.info("Returning {} music bands", bands.size());
            return new CommandResponse("Music bands in collection:", bands, true);
        }
    }

    @Override
    public String getDescription() {
        return "show all music bands in the collection";
    }
}