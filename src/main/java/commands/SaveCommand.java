package commands;

import manager.FileStorage;
import manager.MusicBandCollection;

public class SaveCommand implements Command {
    private final FileStorage storage;
    private final MusicBandCollection collection;

    public SaveCommand(FileStorage storage, MusicBandCollection collection) {
        this.storage = storage;
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        storage.saveToFile(null, collection); // Передаем collection, fileName остается null (используется ранее установленное значение)
    }
}