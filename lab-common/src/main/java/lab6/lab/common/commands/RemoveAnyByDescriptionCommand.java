package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

public class RemoveAnyByDescriptionCommand implements lab6.lab.common.commands.Command {
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