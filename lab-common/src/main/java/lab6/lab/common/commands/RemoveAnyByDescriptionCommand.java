package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Команда для удаления одной группы с указанным описанием.
 */
public class RemoveAnyByDescriptionCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RemoveAnyByDescriptionCommand.class);
    private final MusicBandCollection collection;

    public RemoveAnyByDescriptionCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("Description required for remove_any_by_description command");
            throw new IllegalArgumentException("Description required for remove_any_by_description command");
        }

        try {
            boolean removed = collection.removeAnyByDescription(argument);
            if (removed) {
                logger.info("Removed band with description '{}'", argument);
                System.out.println("Группа с описанием '" + argument + "' удалена.");
            } else {
                logger.info("No band found with description '{}'", argument);
                System.out.println("Группа с описанием '" + argument + "' не найдена.");
            }
        } catch (Exception e) {
            logger.error("Error removing band by description: {}", e.getMessage());
            throw new RuntimeException("Error removing band by description: " + e.getMessage(), e);
        }
    }

    public CommandResponse executeWithResponse(String description) {
        if (description == null || description.trim().isEmpty()) {
            logger.error("Description required for remove_any_by_description command");
            throw new IllegalArgumentException("Description required for remove_any_by_description command");
        }

        try {
            boolean removed = collection.removeAnyByDescription(description);
            if (removed) {
                logger.info("Removed band with description '{}'", description);
                return new CommandResponse("Removed band with description '" + description + "'", null, true);
            } else {
                logger.info("No band found with description '{}'", description);
                return new CommandResponse("No band found with description '" + description + "'", null, true);
            }
        } catch (Exception e) {
            logger.error("Error removing band by description: {}", e.getMessage());
            throw new RuntimeException("Error removing band by description: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "remove one music band with the specified description";
    }
}