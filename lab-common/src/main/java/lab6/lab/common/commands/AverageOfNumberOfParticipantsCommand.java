package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Команда для вычисления среднего количества участников всех групп в коллекции.
 */
public class AverageOfNumberOfParticipantsCommand implements Command {
    private static final Logger logger = LogManager.getLogger(AverageOfNumberOfParticipantsCommand.class);
    private final MusicBandCollection collection;

    public AverageOfNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        double average = collection.getAverageNumberOfParticipants();
        if (average == 0) {
            logger.info("Collection is empty, average is undefined");
            System.out.println("Коллекция пуста, среднее значение не определено.");
        } else {
            logger.info("Average number of participants: {}", average);
            System.out.println("Среднее количество участников: " + average);
        }
    }

    public CommandResponse executeWithResponse() {
        double average = collection.getAverageNumberOfParticipants();
        if (average == 0) {
            logger.info("Collection is empty, average is undefined");
            return new CommandResponse("Collection is empty, average is undefined.", null, true);
        } else {
            logger.info("Average number of participants: {}", average);
            List<Double> result = Collections.singletonList(average);
            return new CommandResponse("Average number of participants:", result, true);
        }
    }

    @Override
    public String getDescription() {
        return "calculate the average number of participants across all music bands";
    }
}