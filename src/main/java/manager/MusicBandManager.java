package manager;

import models.MusicBand;
import common.CommandRequest;

import java.util.List;

public class MusicBandManager {
    private final MusicBandCollection collection;
    private final FileStorage storage;
    private final CommandExecutor executor;

    public MusicBandManager() {
        this.collection = new MusicBandCollection();
        this.storage = new FileStorage(new MusicBandFactory());
        this.executor = new CommandExecutor(collection, storage);
    }

    public void loadFromFile(String fileName) {
        storage.loadFromFile(fileName, collection);
    }

    public void executeCommand(String commandLine) {
        executor.executeCommand(commandLine, null);
    }

    public void executeCommand(CommandRequest request) {
        executor.executeCommand(request);
    }

    public List<MusicBand> getMusicBands() {
        return collection.getMusicBands();
    }

    public MusicBandCollection getCollection() {
        return collection;
    }

    public CommandExecutor getExecutor() {
        if (executor == null) {
            throw new IllegalStateException("CommandExecutor не инициализирован в MusicBandManager.");
        }
        return executor;
    }
}