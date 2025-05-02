package commands;

import manager.MusicBandCollection;

public class FilterGreaterThanNumberOfParticipantsCommand implements Command {
    private final MusicBandCollection collection;

    public FilterGreaterThanNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указано количество участников.");
            }
            int numberOfParticipants = Integer.parseInt(argument);
            System.out.println("Группы с количеством участников больше " + numberOfParticipants + ":");
            collection.getMusicBands().stream()
                    .filter(band -> band.getNumberOfParticipants() > numberOfParticipants)
                    .forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}