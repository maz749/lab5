package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

/**
 * Команда для вставки группы на указанную позицию в коллекции.
 */
public class InsertAtCommand implements Command {
    private final MusicBandCollection collection;

    public InsertAtCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда insert_at требует индекс и объект MusicBand.");
    }

    public void execute(int index, MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        synchronized (collection.getMusicBands()) {
            if (index >= 0 && index <= collection.getMusicBands().size()) {
                collection.getMusicBands().add(index, band);
                System.out.println("Музыкальная группа " + band.getName() + " вставлена на позицию " + index + ".");
            } else {
                System.out.println("Ошибка: Индекс " + index + " вне допустимого диапазона.");
            }
        }
    }
}