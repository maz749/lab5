package commands;

import manager.MusicBandCollection;
import models.MusicBand;

public class ShowCommand implements Command {
    private MusicBandCollection collection;

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