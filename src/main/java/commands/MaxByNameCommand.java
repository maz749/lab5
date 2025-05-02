package commands;

import manager.MusicBandCollection;

public class MaxByNameCommand implements Command {
    private final MusicBandCollection collection;

    public MaxByNameCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        collection.maxByName();
    }
}