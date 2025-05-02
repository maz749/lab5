package commands;

import manager.MusicBandCollection;
import models.MusicBand;

public class RemoveLowerCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveLowerCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда remove_lower требует объект MusicBand и должна вызываться с объектом.");
    }

    public void execute(MusicBand band) {
        try {
            if (band == null) {
                System.out.println("Ошибка: Объект MusicBand не предоставлен.");
                return;
            }
            collection.removeLower(band);
            System.out.println("Элементы, меньшие чем заданный, удалены.");
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении команды remove_lower: " + e.getMessage());
        }
    }
}