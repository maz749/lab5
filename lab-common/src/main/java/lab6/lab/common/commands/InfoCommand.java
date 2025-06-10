package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Команда для вывода информации о коллекции.
 */
public class InfoCommand implements Command {
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);
    private final MusicBandCollection collection;

    public InfoCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        String type = collection.getMusicBands().getClass().getSimpleName();
        int size = collection.getMusicBands().size();
        logger.info("Collection info: type={}, size={}", type, size);
        System.out.println("Тип коллекции: " + type);
        System.out.println("Количество элементов: " + size);
    }

    public CommandResponse executeWithResponse() {
        String type = collection.getMusicBands().getClass().getSimpleName();
        int size = collection.getMusicBands().size();
        logger.info("Collection info: type={}, size={}", type, size);
        List<String> info = Arrays.asList(
                "Type: " + type,
                "Size: " + size
        );
        return new CommandResponse("Collection information:", info, true);
    }

    @Override
    public String getDescription() {
        return "show information about the collection";
    }
}