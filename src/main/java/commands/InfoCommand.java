package commands;

import manager.MusicBandCollection;

import java.util.Date;

public class InfoCommand implements Command {
    private final MusicBandCollection collection;

    public InfoCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Тип коллекции: " + collection.getMusicBands().getClass().getName());
        System.out.println("Дата инициализации: " + new Date());
        System.out.println("Количество элементов: " + collection.getMusicBands().size());
    }
}