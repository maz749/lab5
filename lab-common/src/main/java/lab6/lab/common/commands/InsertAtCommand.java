package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

public class InsertAtCommand implements lab6.lab.common.commands.Command {
    private final MusicBandCollection collection;

    public InsertAtCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда insert_at требует объект MusicBand и индекс.");
    }

    public void execute(int index, MusicBand band) {
        try {
            if (band == null) {
                System.out.println("Ошибка: Объект MusicBand не предоставлен.");
                return;
            }
            if (index < 0 || index > collection.getMusicBands().size()) {
                System.out.println("Ошибка: Индекс вне допустимого диапазона.");
                return;
            }
            collection.getMusicBands().add(index, band);
            System.out.println("Группа добавлена на позицию " + index + ": " + band);
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении команды insert_at: " + e.getMessage());
        }
    }
}