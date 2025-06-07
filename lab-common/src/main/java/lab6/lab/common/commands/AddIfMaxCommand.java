package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

/**
 * Команда для добавления группы, если её количество участников больше максимального в коллекции.
 */
public class AddIfMaxCommand implements Command {
    private final MusicBandCollection collection;

    public AddIfMaxCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда add_if_max требует объект MusicBand.");
    }

    public boolean addIfMax(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        boolean added = collection.addIfMax(band);
        if (added) {
            System.out.println("Музыкальная группа " + band.getName() + " добавлена, так как она максимальна.");
        } else {
            System.out.println("Музыкальная группа " + band.getName() + " не добавлена, так как она не максимальна.");
        }
        return added;
    }
}