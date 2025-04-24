/**
 * Команда для вывода всех элементов коллекции.
 */
package commands;

import manager.MusicBandManager;
import models.MusicBand;

public class ShowCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды ShowCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public ShowCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду вывода всех элементов коллекции.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        if (manager.getMusicBands().isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            for (MusicBand band : manager.getMusicBands()) {
                System.out.println(band);
            }
        }
    }
}