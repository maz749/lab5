package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

/**
 * Команда для удаления группы по ID.
 */
public class RemoveByIdCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveByIdCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            System.out.println("Ошибка: Требуется указать ID.");
            return;
        }
        try {
            int id = Integer.parseInt(argument);
            boolean removed = collection.removeById(id);
            if (removed) {
                System.out.println("Группа с ID " + id + " удалена.");
            } else {
                System.out.println("Группа с ID " + id + " не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        }
    }
}