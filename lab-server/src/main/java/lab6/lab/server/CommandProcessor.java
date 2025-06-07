package lab6.lab.server;

import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.commands.*;
import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.manager.DatabaseManager;
import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CommandProcessor {
    private static final Logger logger = LogManager.getLogger(CommandProcessor.class);
    private final MusicBandCollection collection;
    private final DatabaseManager databaseManager;
    private final CommandExecutor executor;

    public CommandProcessor(MusicBandCollection collection, DatabaseManager databaseManager) {
        if (collection == null || databaseManager == null) {
            throw new IllegalArgumentException("Collection and DatabaseManager cannot be null.");
        }
        this.collection = collection;
        this.databaseManager = databaseManager;
        this.executor = new CommandExecutor(collection, databaseManager);
    }

    public CommandResponse processRequest(CommandRequest request, Long userId) {
        logger.info("Processing request: {} for userId: {}", request.getCommandName(), userId);
        try {
            String command = request.getCommandName().toLowerCase();
            String argument = request.getArgument();
            Object object = request.getObject();

            // Handle commands that don't require authentication
            if (command.equals("help")) {
                HelpCommand helpCommand = (HelpCommand) executor.getCommands().get("help");
                CommandResponse response = helpCommand.executeWithResponse();
                logger.info("Help command executed, returning {} commands", response.getData().size());
                return response;
            }


            if (command.equals("login")) {
                Long loginUserId = ((LoginCommand) executor.getCommands().get("login")).execute(request.getUsername(), request.getPassword());
                CommandResponse response = new CommandResponse(loginUserId != null ? "Login successful" : "Invalid username or password", null, loginUserId != null);
                logger.info("Login command result: success={}, message={}", response.isSuccess(), response.getMessage());
                return response;
            } else if (command.equals("register")) {
                Long newUserId = ((RegisterCommand) executor.getCommands().get("register")).execute(request.getUsername(), request.getPassword());
                CommandResponse response = new CommandResponse(newUserId != null ? "Registration successful" : "Username already exists", null, newUserId != null);
                logger.info("Register command result: success={}, message={}", response.isSuccess(), response.getMessage());
                return response;
            }


            if (userId == null) {
                logger.warn("Authentication failed for command: {}", command);
                return new CommandResponse("Authentication failed: Invalid username or password.", null, false);
            }


            if (command.equals("show") || command.equals("filter_by_number_of_participants") || command.equals("filter_greater_than_number_of_participants")) {
                List<MusicBand> bands;
                if (command.equals("show")) {
                    ShowCommand showCommand = (ShowCommand) executor.getCommands().get("show");
                    CommandResponse response = showCommand.executeWithResponse();
                    logger.info("Show command executed, returning {} elements", response.getData() != null ? response.getData().size() : 0);
                    return response;
                } else if (command.equals("filter_by_number_of_participants")) {
                    if (argument == null || argument.trim().isEmpty()) {
                        logger.warn("Number of participants required for {}", command);
                        return new CommandResponse("Error: Number of participants required.", null, false);
                    }
                    try {
                        int numberOfParticipants = Integer.parseInt(argument);
                        if (numberOfParticipants < 0) {
                            logger.warn("Negative number of participants: {}", numberOfParticipants);
                            return new CommandResponse("Error: Number of participants cannot be negative.", null, false);
                        }
                        bands = collection.filterByNumberOfParticipants(numberOfParticipants);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number format for participants: {}", argument);
                        return new CommandResponse("Error: Number of participants must be a number.", null, false);
                    }
                } else {
                    if (argument == null || argument.trim().isEmpty()) {
                        logger.warn("Number of participants required for {}", command);
                        return new CommandResponse("Error: Number of participants required.", null, false);
                    }
                    try {
                        int numberOfParticipants = Integer.parseInt(argument);
                        if (numberOfParticipants <= 0) {
                            logger.warn("Non-positive number of participants: {}", numberOfParticipants);
                            return new CommandResponse("Error: Number of participants must be greater than 0.", null, false);
                        }
                        bands = collection.getMusicBands().stream()
                                .filter(band -> band.getNumberOfParticipants() > numberOfParticipants)
                                .sorted()
                                .collect(Collectors.toList());
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number format for participants: {}", argument);
                        return new CommandResponse("Error: Number of participants must be a number.", null, false);
                    }
                }
                logger.info("Command {} executed, returning {} elements", command, bands.size());
                return new CommandResponse("Command executed", bands, true);
            }


            if (requiresObject(command)) {
                if (object == null || !(object instanceof MusicBand)) {
                    logger.error("Invalid or null MusicBand object for command: {}", command);
                    return new CommandResponse("Error: Command " + command + " requires a valid MusicBand object.", null, false);
                }
                MusicBand band = (MusicBand) object;
                try {
                    validateMusicBand(band);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid MusicBand object: {}", e.getMessage());
                    return new CommandResponse("Error: Invalid MusicBand object: " + e.getMessage(), null, false);
                }

                switch (command) {
                    case "add":
                        databaseManager.addMusicBand(band, userId);
                        logger.info("Music band {} added by userId: {}", band.getName(), userId);
                        return new CommandResponse("Music band added successfully.", null, true);
                    case "add_if_max":
                        boolean added = databaseManager.addIfMax(band, userId);
                        logger.info("Add_if_max for band {}: added={}, userId={}", band.getName(), added, userId);
                        return new CommandResponse(added ? "Music band added as maximum." : "Music band not added (not maximum).", null, added);
                    case "update":
                        if (argument == null || argument.trim().isEmpty()) {
                            logger.warn("ID required for update command");
                            return new CommandResponse("Error: ID required.", null, false);
                        }
                        try {
                            int id = Integer.parseInt(argument);
                            boolean updated = databaseManager.updateMusicBand(id, band, userId);
                            logger.info("Update command for id {}: updated={}, userId={}", id, updated, userId);
                            return new CommandResponse(updated ? "Music band updated successfully." : "Music band not found or not owned by user.", null, updated);
                        } catch (NumberFormatException e) {
                            logger.error("Invalid ID format: {}", argument);
                            return new CommandResponse("Error: ID must be a number.", null, false);
                        }
                    case "remove_lower":
                        databaseManager.removeLower(band, userId);
                        logger.info("Removed bands with fewer participants than {} by userId: {}", band.getNumberOfParticipants(), userId);
                        return new CommandResponse("Removed bands with fewer participants.", null, true);
                    case "insert_at":
                        if (argument == null || argument.trim().isEmpty()) {
                            logger.warn("Index required for insert_at command");
                            return new CommandResponse("Error: Index required.", null, false);
                        }
                        try {
                            int index = Integer.parseInt(argument);
                            databaseManager.insertAtIndex(index, band, userId);
                            logger.info("Inserted band {} at index {} by userId: {}", band.getName(), index, userId);
                            return new CommandResponse("Music band inserted at index " + index, null, true);
                        } catch (NumberFormatException e) {
                            logger.error("Invalid index format: {}", argument);
                            return new CommandResponse("Error: Index must be a number.", null, false);
                        }
                    default:
                        logger.error("Unknown command with object: {}", command);
                        return new CommandResponse("Error: Unknown command " + command, null, false);
                }
            }

            // Handle other commands
            executor.executeCommand(request);
            logger.info("Command {} executed successfully by userId: {}", command, userId);
            return new CommandResponse("Command executed successfully", null, true);

        } catch (SQLException e) {
            logger.error("Database error processing {}: {}", request.getCommandName(), e.getMessage());
            return new CommandResponse("Database error: " + e.getMessage(), null, false);
        } catch (Exception e) {
            logger.error("Error processing {}: {}", request.getCommandName(), e.getMessage());
            return new CommandResponse("Error: " + e.getMessage(), null, false);
        }
    }

    private boolean requiresObject(String command) {
        return command.equals("add") || command.equals("add_if_max") || command.equals("update") ||
                command.equals("remove_lower") || command.equals("insert_at");
    }

    private void validateMusicBand(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("MusicBand object cannot be null.");
        }
        if (band.getName() == null || band.getName().isEmpty()) {
            throw new IllegalArgumentException("Band name cannot be empty.");
        }
        if (band.getCoordinates() == null) {
            throw new IllegalArgumentException("Coordinates cannot be null.");
        }
        if (band.getNumberOfParticipants() < 0) {
            throw new IllegalArgumentException("Number of participants cannot be negative.");
        }
        if (band.getGenre() == null) {
            throw new IllegalArgumentException("Genre cannot be null.");
        }
        if (band.getBestAlbum() == null || band.getBestAlbum().getLength() <= 0) {
            throw new IllegalArgumentException("Album must be valid with length greater than 0.");
        }
        if (Math.abs(band.getCoordinates().getX()) > 1_000_000) {
            throw new IllegalArgumentException("Coordinate X is too large.");
        }
        if (Math.abs(band.getCoordinates().getY()) > 1_000_000) {
            throw new IllegalArgumentException("Coordinate Y is too large.");
        }
        if (band.getNumberOfParticipants() > 1_000_000) {
            throw new IllegalArgumentException("Number of participants is too large.");
        }
        if (band.getBestAlbum().getLength() > 1_000_000) {
            throw new IllegalArgumentException("Album length is too large.");
        }
    }
}