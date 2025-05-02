package commands;

import manager.MusicBandCollection;

public class RemoveAnyByDescriptionCommand implements Command {
    private final MusicBandCollection collection;

    public RemoveAnyByDescriptionCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            System.out.println("Не указано описание для удаления.");
            return;
        }
        if (collection.removeAnyByDescription(argument)) {
            System.out.println("Группа с описанием '" + argument + "' удалена.");
        } else {
            System.out.println("Группа с описанием '" + argument + "' не найдена.");
        }
    }
}