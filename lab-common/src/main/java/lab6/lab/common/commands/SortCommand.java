package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

public class SortCommand implements lab6.lab.common.commands.Command {
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
