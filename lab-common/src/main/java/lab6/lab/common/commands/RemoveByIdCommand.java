package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

public class RemoveByIdCommand implements lab6.lab.common.commands.Command {
    private final MusicBandCollection collection;

    public RemoveByIdCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указан ID для удаления.");
            }
            int id = Integer.parseInt(argument);
            if (collection.removeById(id)) {
                System.out.println("Группа с ID " + id + " удалена.");
            } else {
                System.out.println("Группа с ID " + id + " не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
