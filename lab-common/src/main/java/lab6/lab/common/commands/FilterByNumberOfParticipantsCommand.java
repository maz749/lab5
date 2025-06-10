package lab6.lab.common.commands;

import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Команда для фильтрации групп по количеству участников.
 */
public class FilterByNumberOfParticipantsCommand implements Command {
    private static final Logger logger = LogManager.getLogger(FilterByNumberOfParticipantsCommand.class);
    private final MusicBandCollection collection;

    public FilterByNumberOfParticipantsCommand(MusicBandCollection collection) {
        this.collection = collection;
    }

    @Override
    public void execute(String argument, Object object) {
        if (argument == null || argument.trim().isEmpty()) {
            logger.error("Number of participants required for filter_by_number_of_participants command");
            throw new IllegalArgumentException("Number of participants required for filter_by_number_of_participants command");
        }

        try {
            int numberOfParticipants = Integer.parseInt(argument);
            if (numberOfParticipants < 0) {
                logger.error("Invalid number of participants: {}", numberOfParticipants);
                throw new IllegalArgumentException("Number of participants cannot be negative");
            }

            List<MusicBand> filtered = collection.filterByNumberOfParticipants(numberOfParticipants);
            if (filtered.isEmpty()) {
                logger.info("No bands found with {} participants", numberOfParticipants);
                System.out.println("Группы с количеством участников " + numberOfParticipants + " не найдены.");
            } else {
                logger.info("Found {} bands with {} participants", filtered.size(), numberOfParticipants);
                filtered.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid number format: {}", argument);
            throw new IllegalArgumentException("Number of participants must be a number");
        } catch (Exception e) {
            logger.error("Error filtering bands: {}", e.getMessage());
            throw new RuntimeException("Error filtering bands: " + e.getMessage(), e);
        }
    }

    public CommandResponse executeWithResponse(String numberOfParticipantsStr) {
        if (numberOfParticipantsStr == null || numberOfParticipantsStr.trim().isEmpty()) {
            logger.error("Number of participants required for filter_by_number_of_participants command");
            throw new IllegalArgumentException("Number of participants required for filter_by_number_of_participants command");
        }

        try {
            int numberOfParticipants = Integer.parseInt(numberOfParticipantsStr);
            if (numberOfParticipants < 0) {
                logger.error("Invalid number of participants: {}", numberOfParticipants);
                throw new IllegalArgumentException("Number of participants cannot be negative");
            }

            List<MusicBand> filtered = collection.filterByNumberOfParticipants(numberOfParticipants);
            if (filtered.isEmpty()) {
                logger.info("No bands found with {} participants", numberOfParticipants);
                return new CommandResponse("No bands found with " + numberOfParticipants + " participants.", null, true);
            } else {
                logger.info("Found {} bands with {} participants", filtered.size(), numberOfParticipants);
                return new CommandResponse("Bands with " + numberOfParticipants + " participants:", filtered, true);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid number format: {}", numberOfParticipantsStr);
            throw new IllegalArgumentException("Number of participants must be a number");
        } catch (Exception e) {
            logger.error("Error filtering bands: {}", e.getMessage());
            throw new RuntimeException("Error filtering bands: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "show all music bands with the specified number of participants";
    }
}