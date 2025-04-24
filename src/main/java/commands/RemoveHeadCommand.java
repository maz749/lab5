/**
 * Команда для удаления первого элемента коллекции.
 */
package commands;

import manager.MusicBandManager;
import models.MusicBand;

public class RemoveHeadCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды RemoveHeadCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public RemoveHeadCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду удаления первого элемента.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        MusicBand removedBand = manager.removeHead();
        if (removedBand != null) {
            System.out.println("Удален первый элемент: " + removedBand);
        } else {
            System.out.println("Коллекция пуста.");
        }
    }
}