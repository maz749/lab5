package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class AddCommand implements Command {
    private final MusicBandCollection collection;

    public AddCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда add требует объект MusicBand.");
    }

    public void execute(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        collection.add(band);
        System.out.println("Музыкальная группа " + band.getName() + " добавлена в коллекцию.");
    }
}