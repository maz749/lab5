package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

/**
 * Команда для удаления групп с количеством участников меньше заданного.
 */
public class RemoveLowerCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveLowerCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда remove_lower требует объект MusicBand.");
    }

    public void execute(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        }
        collection.removeLower(band);
        System.out.println("Удалены все группы с количеством участников меньше " + band.getNumberOfParticipants() + ".");
    }
}