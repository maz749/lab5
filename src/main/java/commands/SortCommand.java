/**
 * Команда для сортировки коллекции в естественном порядке.
 */
package commands;

import manager.MusicBandManager;

public class SortCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды SortCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public SortCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду сортировки коллекции.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        manager.getMusicBands().sort(null);
        System.out.println("Коллекция отсортирована по имени.");
    }
}