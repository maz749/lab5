package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class ShowCommand implements lab6.lab.common.commands.Command {
    private final MusicBandCollection collection;

    public ShowCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (collection.getMusicBands().isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            for (MusicBand band : collection.getMusicBands()) {
                System.out.println(band);
            }
        }
    }
}
