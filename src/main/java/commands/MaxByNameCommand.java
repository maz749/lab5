/**
 * Команда для вывода любого объекта из коллекции, значение поля name которого является максимальным.
 */
package commands;

import manager.MusicBandManager;

public class MaxByNameCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды MaxByNameCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public MaxByNameCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду вывода группы с максимальным именем.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        manager.maxByName();
    }
}