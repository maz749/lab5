package manager;

import models.MusicBand;

import java.util.List;

/**
 * Фасад для управления коллекцией музыкальных групп.
 */
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

    public List<MusicBand> getMusicBands() {
        return collection.getMusicBands();
    }
}