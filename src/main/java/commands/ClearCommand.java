/**
 * Команда для очистки коллекции.
 */
package commands;

import manager.MusicBandManager;

public class ClearCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды ClearCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public ClearCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду очистки коллекции.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        manager.clear();
        System.out.println("Коллекция очищена.");
    }
}