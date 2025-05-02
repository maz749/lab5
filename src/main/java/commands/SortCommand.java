package commands;

import manager.MusicBandCollection;

public class SortCommand implements Command {
    private final MusicBandCollection collection;

    public SortCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        collection.sort();
        System.out.println("Коллекция отсортирована по имени.");
    }
}