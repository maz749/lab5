package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для фильтрации групп с количеством участников больше заданного.
 */
public class FilterGreaterThanNumberOfParticipantsCommand implements Command {
    private final MusicBandCollection collection;

    public FilterGreaterThanNumberOfParticipantsCommand(MusicBandCollection collection) {
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
            if (numberOfParticipants <= 0) {
                System.out.println("Ошибка: Количество участников должно быть больше 0.");
                return;
            }
            List<MusicBand> filtered = collection.getMusicBands().stream()
                    .filter(band -> band.getNumberOfParticipants() > numberOfParticipants)
                    .sorted()
                    .collect(Collectors.toList());
            if (filtered.isEmpty()) {
                System.out.println("Группы с количеством участников больше " + numberOfParticipants + " не найдены.");
            } else {
                filtered.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        }
    }
}