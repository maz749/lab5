package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

/**
 * Команда для удаления одной группы с указанным описанием.
 */
public class RemoveAnyByDescriptionCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveAnyByDescriptionCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            System.out.println("Ошибка: Требуется указать описание.");
            return;
        }
        boolean removed = collection.removeAnyByDescription(argument);
        if (removed) {
            System.out.println("Группа с описанием '" + argument + "' удалена.");
        } else {
            System.out.println("Группа с описанием '" + argument + "' не найдена.");
        }
    }
}