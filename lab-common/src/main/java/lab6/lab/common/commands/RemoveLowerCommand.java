package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class RemoveLowerCommand implements lab6.lab.common.commands.Command {
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