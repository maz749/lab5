package lab6.lab.common.manager;

import lab6.lab.common.models.MusicBand;
import lab6.lab.common.database.MusicBandRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class MusicBandManager {
    private static final Logger logger = LogManager.getLogger(MusicBandManager.class);
    private final MusicBandCollection collection;
    private final MusicBandRepository musicBandRepository;
    private final CommandExecutor executor;

    public MusicBandManager() throws SQLException {
        this.collection = new MusicBandCollection();
        this.musicBandRepository = new MusicBandRepository();
        this.executor = new CommandExecutor(collection, musicBandRepository);
        loadCollection();
    }

    public MusicBandManager(MusicBandCollection collection) throws SQLException {
        this.collection = collection;
        this.musicBandRepository = new MusicBandRepository();
        this.executor = new CommandExecutor(collection, musicBandRepository);
        loadCollection();
    }

    private void loadCollection() throws SQLException {
        collection.clear();
        List<MusicBand> bands = musicBandRepository.getAllBands();
        for (MusicBand band : bands) {
            collection.add(band);
        }
        logger.info("Loaded {} music bands from database", collection.size());
    }

    public void executeCommand(String commandLine) {
        executor.executeCommand(commandLine, null);
    }

    public List<MusicBand> getMusicBands() {
        return collection.getMusicBands();
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public MusicBandRepository getMusicBandRepository() {
        return musicBandRepository;
    }

    public MusicBandCollection getCollection() {
        return collection;
    }
}