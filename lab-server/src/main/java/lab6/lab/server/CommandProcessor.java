package lab6.lab.server;

import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.commands.*;
import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.database.UserRepository;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandProcessor {
    private static final Logger logger = LogManager.getLogger(CommandProcessor.class);
    private final MusicBandCollection collection;
    private final UserRepository userRepository;
    private final MusicBandRepository musicBandRepository;
    private final CommandExecutor executor;
    private static final Set<String> READ_ONLY_COMMANDS = Set.of(
            "help", "info", "show", "filter_by_description",
            "print_field_descending_number_of_participants", "history"
    );

    public CommandProcessor(MusicBandCollection collection, UserRepository userRepository, MusicBandRepository musicBandRepository) {
        if (collection == null || userRepository == null || musicBandRepository == null) {
            throw new IllegalArgumentException("Collection and repositories cannot be null.");
        }
        this.collection = collection;
        this.userRepository = userRepository;
        this.musicBandRepository = musicBandRepository;
        this.executor = new CommandExecutor(collection, musicBandRepository);
    }

    public CommandResponse processRequest(CommandRequest request, Long userId) {
        if (userId == null) {
            return new CommandResponse("Authentication required", null, false);
        }

        String command = request.getCommandName().toLowerCase();
        String argument = request.getArgument();
        Object requestObject = request.getObject();

        try {
            // Check if command exists
            Command commandObj = executor.getCommands().get(command);
            if (commandObj == null) {
                return new CommandResponse("Unknown command: " + command, null, false);
            }

            // Special handling for help and show commands
            if (command.equals("help") && commandObj instanceof HelpCommand) {
                return ((HelpCommand) commandObj).executeWithResponse();
            }
            if (command.equals("show") && commandObj instanceof ShowCommand) {
                return ((ShowCommand) commandObj).executeWithResponse();
            }

            // Check permissions for modification commands
            if (!READ_ONLY_COMMANDS.contains(command)) {
                // For commands that work with MusicBand objects
                if (requestObject instanceof MusicBand) {
                    MusicBand band = (MusicBand) requestObject;
                    if (!canModifyBand(band, userId)) {
                        return new CommandResponse("You don't have permission to modify this band", null, false);
                    }
                }
                // For commands that work with band IDs
                else if (command.equals("remove_by_id") || command.equals("update")) {
                    try {
                        int bandId = Integer.parseInt(argument);
                        MusicBand existingBand = collection.getMusicBands().stream()
                                .filter(b -> b.getId() == bandId)
                                .findFirst()
                                .orElse(null);
                        if (existingBand != null && existingBand.getUserId() != userId) {
                            return new CommandResponse("You don't have permission to modify this band", null, false);
                        }
                    } catch (NumberFormatException e) {
                        return new CommandResponse("Invalid band ID format", null, false);
                    }
                }
                // For commands that modify multiple bands
                else if (command.equals("clear")) {
                    // Clear only removes user's own bands
                    collection.getMusicBands().removeIf(band -> band.getUserId() != userId);
                }
                else if (command.equals("remove_lower")) {
                    if (requestObject instanceof MusicBand) {
                        MusicBand band = (MusicBand) requestObject;
                        // Remove only user's own bands that are lower than the given one
                        collection.getMusicBands().removeIf(b ->
                                b.getUserId() == userId &&
                                        b.getNumberOfParticipants() < band.getNumberOfParticipants()
                        );
                    }
                }
                else if (command.equals("remove_any_by_description")) {
                    if (argument != null) {
                        // Remove only one of user's own bands with matching description
                        collection.getMusicBands().stream()
                                .filter(b -> b.getUserId() == userId &&
                                        b.getDescription() != null &&
                                        b.getDescription().equals(argument))
                                .findFirst()
                                .ifPresent(band -> collection.removeById(band.getId()));
                    }
                }
                else if (command.equals("add_if_max")) {
                    if (requestObject instanceof MusicBand) {
                        MusicBand band = (MusicBand) requestObject;
                        // Check if the band would be max among user's own bands
                        boolean isMax = collection.getMusicBands().stream()
                                .filter(b -> b.getUserId() == userId)
                                .allMatch(b -> b.getNumberOfParticipants() < band.getNumberOfParticipants());
                        if (!isMax) {
                            return new CommandResponse("The band is not the maximum among your bands", null, false);
                        }
                    }
                }
            }

            // Execute command and handle response
            try {
                commandObj.execute(argument, requestObject);
                return new CommandResponse("Command executed successfully", null, true);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid argument for command {}: {}", command, e.getMessage());
                return new CommandResponse("Error: " + e.getMessage(), null, false);
            } catch (Exception e) {
                logger.error("Error executing command {}: {}", command, e.getMessage());
                return new CommandResponse("Error executing command: " + e.getMessage(), null, false);
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing {}: {}", command, e.getMessage());
            return new CommandResponse("Error: " + e.getMessage(), null, false);
        }
    }

    private boolean canModifyBand(MusicBand band, Long userId) {
        // New bands can be created by any authenticated user
        if (band.getId() == 0) { // Assuming 0 is the default value for new bands
            return true;
        }

        // Existing bands can only be modified by their owner
        MusicBand existingBand = collection.getMusicBands().stream()
                .filter(b -> b.getId() == band.getId())
                .findFirst()
                .orElse(null);

        return existingBand != null && existingBand.getUserId() == userId;
    }
}