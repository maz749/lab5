package commands;

import manager.FileStorage;

public class SaveCommand implements Command {
    private FileStorage storage;

    public SaveCommand(FileStorage storage) {
        this.storage = storage;
    }

    @Override
    public void execute(String argument) {
        storage.saveToFile(null, null);
    }
}