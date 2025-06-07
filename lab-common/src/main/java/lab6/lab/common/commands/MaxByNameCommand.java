package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

/**
 * Команда для вывода группы с максимальным именем (по алфавиту).
 */
public class MaxByNameCommand implements Command {
    private final MusicBandCollection collection;

    public MaxByNameCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        MusicBand maxBand = collection.getMaxByName();
        if (maxBand == null) {
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println("Группа с максимальным именем: " + maxBand);
        }
    }
}