package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class RemoveHeadCommand implements lab6.lab.common.commands.Command {
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
