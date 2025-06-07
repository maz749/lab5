package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

/**
 * Команда для вывода информации о коллекции.
 */
public class InfoCommand implements Command {
    private final MusicBandCollection collection;

    public InfoCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Тип коллекции: " + collection.getMusicBands().getClass().getSimpleName());
        System.out.println("Количество элементов: " + collection.getMusicBands().size());
    }
}