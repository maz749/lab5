package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class UpdateCommand implements Command {
    private final MusicBandCollection collection;

    public UpdateCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда update требует ID и объект MusicBand.");
    }

    public void execute(int id, MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        collection.update(id, band);
        System.out.println("Музыкальная группа с ID " + id + " обновлена.");
    }
}