/**
 * Команда для вывода элементов, значение поля numberOfParticipants которых больше заданного.
 */
package commands;

import manager.MusicBandManager;

public class FilterGreaterThanNumberOfParticipantsCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды FilterGreaterThanNumberOfParticipantsCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public FilterGreaterThanNumberOfParticipantsCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду фильтрации по количеству участников больше заданного.
     *
     * @param argument количество участников для фильтрации
     */
    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указано количество участников.");
            }
            int numberOfParticipants = Integer.parseInt(argument);
            System.out.println("Группы с количеством участников больше " + numberOfParticipants + ":");
            manager.getMusicBands().stream()
                    .filter(band -> band.getNumberOfParticipants() > numberOfParticipants)
                    .forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Количество участников должно быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}