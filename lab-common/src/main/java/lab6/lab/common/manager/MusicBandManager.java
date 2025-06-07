package lab6.lab.common.manager;

import lab6.lab.common.models.MusicBand;

import java.sql.SQLException;
import java.util.List;

public class MusicBandManager {
    private final MusicBandCollection collection;
    private final DatabaseManager dbManager;
    private final CommandExecutor executor;


    public MusicBandManager() throws SQLException {
        this.collection = new MusicBandCollection();
        this.dbManager = new DatabaseManager(collection);
        this.executor = new CommandExecutor(collection, dbManager);
        dbManager.loadCollection();
    }

    public MusicBandManager(MusicBandCollection collection) throws SQLException {
        this.collection = collection;
        this.dbManager = new DatabaseManager(collection);
        this.executor = new CommandExecutor(collection, dbManager);
        dbManager.loadCollection();
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

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public MusicBandCollection getCollection() {
        return collection;
    }
}