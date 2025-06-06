package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

public class MaxByNameCommand implements lab6.lab.common.commands.Command {
    private final MusicBandCollection collection;

    public MaxByNameCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        collection.maxByName();
    }
}
