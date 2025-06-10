package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Команда для вывода группы с максимальным именем (по алфавиту).
 */
public class MaxByNameCommand implements Command {
    private static final Logger logger = LogManager.getLogger(MaxByNameCommand.class);
    private final MusicBandCollection collection;

    public MaxByNameCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        MusicBand maxBand = collection.getMaxByName();
        if (maxBand == null) {
            logger.info("Collection is empty, no max band found");
            System.out.println("Коллекция пуста.");
        } else {
            logger.info("Found max band by name: id={}, name={}", maxBand.getId(), maxBand.getName());
            System.out.println("Группа с максимальным именем: " + maxBand);
        }
    }

    public CommandResponse executeWithResponse() {
        MusicBand maxBand = collection.getMaxByName();
        if (maxBand == null) {
            logger.info("Collection is empty, no max band found");
            return new CommandResponse("Collection is empty.", null, true);
        } else {
            logger.info("Found max band by name: id={}, name={}", maxBand.getId(), maxBand.getName());
            List<MusicBand> result = Collections.singletonList(maxBand);
            return new CommandResponse("Band with maximum name:", result, true);
        }
    }

    @Override
    public String getDescription() {
        return "show the music band with the maximum name (alphabetically)";
    }
}