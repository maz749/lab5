package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Команда для удаления первой группы в коллекции.
 */
public class RemoveHeadCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RemoveHeadCommand.class);
    private final MusicBandCollection collection;

    public RemoveHeadCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        MusicBand head = collection.removeHead();
        if (head == null) {
            logger.info("Collection is empty, nothing to remove");
            System.out.println("Коллекция пуста.");
        } else {
            logger.info("Removed head band: id={}, name={}", head.getId(), head.getName());
            System.out.println("Удалена первая группа: " + head);
        }
    }

    public CommandResponse executeWithResponse() {
        MusicBand head = collection.removeHead();
        if (head == null) {
            logger.info("Collection is empty, nothing to remove");
            return new CommandResponse("Collection is empty.", null, true);
        } else {
            logger.info("Removed head band: id={}, name={}", head.getId(), head.getName());
            List<MusicBand> result = Collections.singletonList(head);
            return new CommandResponse("Removed first band:", result, true);
        }
    }

    @Override
    public String getDescription() {
        return "remove the first music band from the collection";
    }
}