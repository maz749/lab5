package commands;

import manager.MusicBandCollection;
import models.MusicBand;

public class RemoveHeadCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveHeadCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        MusicBand removedBand = collection.removeHead();
        if (removedBand != null) {
            System.out.println("Удален первый элемент: " + removedBand);
        } else {
            System.out.println("Коллекция пуста.");
        }
    }
}