/**
 * Команда для вывода информации о коллекции.
 */
package commands;

import manager.MusicBandManager;

import java.util.Date;

public class InfoCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды InfoCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public InfoCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду вывода информации о коллекции.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        System.out.println("Тип коллекции: " + manager.getMusicBands().getClass().getName());
        System.out.println("Дата инициализации: " + new Date());
        System.out.println("Количество элементов: " + manager.getMusicBands().size());
    }
}