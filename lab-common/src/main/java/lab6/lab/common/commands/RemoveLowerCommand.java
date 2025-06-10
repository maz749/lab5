package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для удаления групп с количеством участников меньше заданного.
 */
public class RemoveLowerCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RemoveLowerCommand.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;

    public RemoveLowerCommand(MusicBandCollection collection, MusicBandRepository musicBandRepository) {
        this.collection = collection;
        this.musicBandRepository = musicBandRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        if (!(object instanceof MusicBand)) {
            logger.error("Invalid object type for remove_lower command");
            throw new IllegalArgumentException("Command requires a MusicBand object");
        }

        MusicBand band = (MusicBand) object;
        try {
            // Note: userId will be set by the CommandProcessor
            List<MusicBand> bandsToRemove = collection.getMusicBands().stream()
                    .filter(b -> b.getNumberOfParticipants() < band.getNumberOfParticipants())
                    .collect(Collectors.toList());

            for (MusicBand b : bandsToRemove) {
                collection.removeById(b.getId());
            }

            logger.info("Removed {} bands with fewer participants than {}", bandsToRemove.size(), band.getNumberOfParticipants());
        } catch (Exception e) {
            logger.error("Error in remove_lower command: {}", e.getMessage());
            throw new RuntimeException("Error in remove_lower command: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "remove all music bands with fewer participants than the given one";
    }
}