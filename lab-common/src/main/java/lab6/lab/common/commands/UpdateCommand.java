package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class UpdateCommand implements lab6.lab.common.commands.Command {
    private final MusicBandCollection collection;

    public UpdateCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            System.out.println("Ошибка: Укажите ID музыкальной группы для обновления.");
            return;
        }
        System.out.println("Команда update требует объект MusicBand и должна вызываться с объектом.");
    }

    public void execute(int id, MusicBand updatedBand) {
        if (updatedBand == null) {
            System.out.println("Ошибка: Объект MusicBand не предоставлен.");
            return;
        }
        try {
            MusicBand existingBand = collection.getMusicBandById(id);
            if (existingBand == null) {
                System.out.println("Музыкальная группа с ID " + id + " не найдена.");
                return;
            }
            collection.update(id, updatedBand);
            System.out.println("Музыкальная группа с ID " + id + " успешно обновлена: " + updatedBand);
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении музыкальной группы: " + e.getMessage());
        }
    }
}