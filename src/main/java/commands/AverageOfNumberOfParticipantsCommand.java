package commands;

import manager.MusicBandCollection;

public class AverageOfNumberOfParticipantsCommand implements Command {
    private MusicBandCollection collection;

    public AverageOfNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        if (collection.getMusicBands().isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        double average = collection.getAverageNumberOfParticipants();
        System.out.println("Среднее количество участников: " + average);
    }
}