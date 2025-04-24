package commands;

import manager.MusicBandCollection;

public class FilterByNumberOfParticipantsCommand implements Command {
    private MusicBandCollection collection;

    public FilterByNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указано количество участников для фильтрации.");
            }
            int numberOfParticipants = Integer.parseInt(argument);
            System.out.println("Группы с количеством участников " + numberOfParticipants + ":");
            collection.filterByNumberOfParticipants(numberOfParticipants)
                    .forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}