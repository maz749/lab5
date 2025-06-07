package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;

/**
 * Команда для вычисления среднего количества участников всех групп в коллекции.
 */
public class AverageOfNumberOfParticipantsCommand implements Command {
    private final MusicBandCollection collection;

    public AverageOfNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument) {
        double average = collection.getAverageNumberOfParticipants();
        if (average == 0) {
            System.out.println("Коллекция пуста, среднее значение не определено.");
        } else {
            System.out.println("Среднее количество участников: " + average);
        }
    }
}