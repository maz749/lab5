package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

import java.util.List;

/**
 * Команда для фильтрации групп по количеству участников.
 */
public class FilterByNumberOfParticipantsCommand implements Command {
    private final MusicBandCollection collection;

    public FilterByNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            System.out.println("Ошибка: Требуется указать количество участников.");
            return;
        }
        try {
            int numberOfParticipants = Integer.parseInt(argument);
            if (numberOfParticipants < 0) {
                System.out.println("Ошибка: Количество участников не может быть отрицательным.");
                return;
            }
            List<MusicBand> filtered = collection.filterByNumberOfParticipants(numberOfParticipants);
            if (filtered.isEmpty()) {
                System.out.println("Группы с количеством участников " + numberOfParticipants + " не найдены.");
            } else {
                filtered.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        }
    }
}