package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

public class ClearCommand implements Command {
    private final MusicBandCollection collection;

    public ClearCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        collection.clear();
        System.out.println("Коллекция очищена.");
    }
}
