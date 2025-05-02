package commands;

import manager.MusicBandCollection;
import models.MusicBand;

import java.io.BufferedReader;

public class AddIfMaxCommand implements Command {
    private final MusicBandCollection collection;

    public AddIfMaxCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        throw new UnsupportedOperationException("Команда add_if_max требует объект MusicBand.");
    }

    @Override
    public void execute(String argument, BufferedReader reader) {
        throw new UnsupportedOperationException("Команда add_if_max на сервере не поддерживает консольный ввод.");
    }

    public void execute(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не предоставлен.");
        }
        if (collection.addIfMax(band)) {
            System.out.println("Группа добавлена на сервере, так как она имеет максимальное количество участников: " + band);
        } else {
            System.out.println("Группа не добавлена, так как она не имеет максимального количества участников.");
        }
    }
}