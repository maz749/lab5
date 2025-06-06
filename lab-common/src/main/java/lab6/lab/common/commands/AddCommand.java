package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

import java.io.BufferedReader;

public class AddCommand implements Command {
    private final MusicBandCollection collection;

    public AddCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        throw new UnsupportedOperationException("Команда add требует объект MusicBand и должна вызываться с объектом.");
    }

    @Override
    public void execute(String argument, BufferedReader reader) {
        throw new UnsupportedOperationException("Команда add на сервере не поддерживает консольный ввод.");
    }

    public void execute(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не предоставлен.");
        }
        collection.add(band);
        System.out.println("Музыкальная группа добавлена на сервере: " + band);
    }
}