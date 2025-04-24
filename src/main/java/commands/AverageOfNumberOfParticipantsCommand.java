/**
 * Команда для вывода среднего значения поля numberOfParticipants для всех элементов коллекции.
 */
package commands;

import manager.MusicBandManager;
import models.MusicBand;

public class AverageOfNumberOfParticipantsCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды AverageOfNumberOfParticipantsCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public AverageOfNumberOfParticipantsCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду вывода среднего значения поля numberOfParticipants.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        if (manager.getMusicBands().isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        double average = manager.getMusicBands().stream()
                .mapToInt(MusicBand::getNumberOfParticipants)
                .average()
                .orElse(0);
        System.out.println("Среднее количество участников: " + average);
    }
}