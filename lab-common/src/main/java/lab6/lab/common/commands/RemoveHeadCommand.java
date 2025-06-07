package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

/**
 * Команда для удаления первой группы в коллекции.
 */
public class RemoveHeadCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveHeadCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        MusicBand head = collection.removeHead();
        if (head == null) {
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println("Удалена первая группа: " + head);
        }
    }
}