package lab6.lab.common.commands;

import lab6.lab.common.manager.FileStorage;

public class SaveCommand implements lab6.lab.common.commands.Command {
    private FileStorage storage;

    public SaveCommand(FileStorage storage) {
        this.storage = storage;
    }

    @Override
    public void execute(String argument) {
        storage.saveToFile(null, null);
    }
}
