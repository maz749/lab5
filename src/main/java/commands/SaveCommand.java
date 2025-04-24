package commands;

import manager.MusicBandManager;

public class SaveCommand implements Command {
    private MusicBandManager manager;

    public SaveCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String argument) {
        manager.saveToFile(null);
    }
}