/**
 * Команда для удаления из коллекции одного элемента, значение поля description которого эквивалентно заданному.
 */
package commands;

import manager.MusicBandManager;

public class RemoveAnyByDescriptionCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды RemoveAnyByDescriptionCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public RemoveAnyByDescriptionCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду удаления группы по описанию.
     *
     * @param argument описание для удаления
     */
    @Override
    public void execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            System.out.println("Не указано описание для удаления.");
            return;
        }
        if (manager.removeAnyByDescription(argument)) {
            System.out.println("Группа с описанием '" + argument + "' удалена.");
        } else {
            System.out.println("Группа с описанием '" + argument + "' не найдена.");
        }
    }
}