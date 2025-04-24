/**
 * Команда для вывода элементов, значение поля numberOfParticipants которых равно заданному.
 */
package commands;

import manager.MusicBandManager;

public class FilterByNumberOfParticipantsCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды FilterByNumberOfParticipantsCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public FilterByNumberOfParticipantsCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду фильтрации по количеству участников.
     *
     * @param argument количество участников для фильтрации
     */
    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указано количество участников для фильтрации.");
            }
            int numberOfParticipants = Integer.parseInt(argument);
            System.out.println("Группы с количеством участников " + numberOfParticipants + ":");
            manager.getMusicBands().stream()
                    .filter(band -> band.getNumberOfParticipants() == numberOfParticipants)
                    .forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}