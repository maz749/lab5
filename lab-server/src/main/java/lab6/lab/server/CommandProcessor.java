//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.server;

import java.util.List;
import java.util.stream.Collectors;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.commands.AddCommand;
import lab6.lab.common.commands.AddIfMaxCommand;
import lab6.lab.common.commands.Command;
import lab6.lab.common.commands.InsertAtCommand;
import lab6.lab.common.commands.RemoveLowerCommand;
import lab6.lab.common.commands.UpdateCommand;
import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.manager.MusicBandManager;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandProcessor {
    private static final Logger logger = LogManager.getLogger(CommandProcessor.class);
    private final MusicBandManager manager;

    public CommandProcessor(MusicBandManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("MusicBandManager не может быть null.");
        } else {
            this.manager = manager;
        }
    }

    public CommandResponse processRequest(CommandRequest request) {
        logger.info("Начало обработки запроса: {}", request.getCommandName());

        try {
            CommandResponse var3;
            try {
                String command = request.getCommandName().toLowerCase();
                String argument = request.getArgument();
                Object object = request.getObject();
                CommandResponse var32;
                if (command.equals("execute_script")) {
                    var32 = new CommandResponse("Ошибка: Команда execute_script не поддерживается на сервере.", (List)null, false);
                    return var32;
                } else {
                    CommandResponse var7;
                    if (command.equals("show") || command.equals("filter_by_number_of_participants") || command.equals("filter_greater_than_number_of_participants")) {
                        List bands;
                        int numberOfParticipants;
                        CommandResponse var36;
                        if (command.equals("filter_by_number_of_participants")) {
                            if (argument == null || argument.trim().isEmpty()) {
                                var36 = new CommandResponse("Ошибка: Требуется указать количество участников.", (List)null, false);
                                return var36;
                            }

                            try {
                                numberOfParticipants = Integer.parseInt(argument);
                                if (numberOfParticipants < 0) {
                                    var7 = new CommandResponse("Ошибка: Количество участников не может быть отрицательным.", (List)null, false);
                                    return var7;
                                }

                                bands = (List)this.manager.getMusicBands().stream().filter((bandx) -> {
                                    return bandx.getNumberOfParticipants() == numberOfParticipants;
                                }).sorted().collect(Collectors.toList());
                            } catch (NumberFormatException var25) {
                                var7 = new CommandResponse("Ошибка: Количество участников должно быть числом.", (List)null, false);
                                return var7;
                            }
                        } else if (command.equals("filter_greater_than_number_of_participants")) {
                            if (argument == null || argument.trim().isEmpty()) {
                                var36 = new CommandResponse("Ошибка: Требуется указать количество участников.", (List)null, false);
                                return var36;
                            }

                            try {
                                numberOfParticipants = Integer.parseInt(argument);
                                if (numberOfParticipants <= 0) {
                                    var7 = new CommandResponse("Ошибка: Количество участников должно быть больше 0.", (List)null, false);
                                    return var7;
                                }

                                bands = (List)this.manager.getMusicBands().stream().filter((bandx) -> {
                                    return bandx.getNumberOfParticipants() > numberOfParticipants;
                                }).sorted().collect(Collectors.toList());
                            } catch (NumberFormatException var23) {
                                var7 = new CommandResponse("Ошибка: Количество участников должно быть числом.", (List)null, false);
                                return var7;
                            }
                        } else {
                            bands = this.manager.getMusicBands();
                        }

                        logger.info("Команда {} выполнена, возвращен список из {} элементов", command, bands.size());
                        var36 = new CommandResponse("Команда выполнена", bands, true);
                        return var36;
                    } else if (!this.requiresObject(command)) {
                        this.manager.executeCommand(this.buildCommandLine(command, argument));
                        logger.info("Команда {} выполнена", command);
                        var32 = new CommandResponse("Команда успешно выполнена", (List)null, true);
                        return var32;
                    } else if (!(object instanceof MusicBand)) {
                        var32 = new CommandResponse("Ошибка: Команда " + command + " требует объект MusicBand.", (List)null, false);
                        return var32;
                    } else {
                        MusicBand band = (MusicBand)object;

                        try {
                            this.validateMusicBand(band);
                        } catch (IllegalArgumentException var24) {
                            IllegalArgumentException e = var24;
                            var7 = new CommandResponse("Ошибка: Некорректный объект MusicBand: " + e.getMessage(), (List)null, false);
                            return var7;
                        }

                        CommandExecutor executor = this.manager.getExecutor();
                        if (executor == null) {
                            var7 = new CommandResponse("Ошибка: CommandExecutor не инициализирован.", (List)null, false);
                            return var7;
                        } else {
                            Command cmd = (Command)executor.getCommands().get(command);
                            if (cmd == null) {
                                CommandResponse var8 = new CommandResponse("Ошибка: Команда " + command + " не найдена.", (List)null, false);
                                return var8;
                            } else {
                                CommandResponse var10;
                                CommandResponse var11;
                                int index;
                                switch (command) {
                                    case "add":
                                        if (!(cmd instanceof AddCommand)) {
                                            var10 = new CommandResponse("Ошибка: Команда add имеет неверный тип.", (List)null, false);
                                            return var10;
                                        }

                                        ((AddCommand)cmd).execute(band);
                                        logger.info("Добавлена новая музыкальная группа: {}", band.getName());
                                        var10 = new CommandResponse("Музыкальная группа успешно добавлена.", (List)null, true);
                                        return var10;
                                    case "add_if_max":
                                        if (cmd instanceof AddIfMaxCommand) {
                                            ((AddIfMaxCommand)cmd).execute(band);
                                            logger.info("Команда add_if_max выполнена для группы: {}", band.getName());
                                            var10 = new CommandResponse("Команда add_if_max выполнена.", (List)null, true);
                                            return var10;
                                        }

                                        var10 = new CommandResponse("Ошибка: Команда add_if_max имеет неверный тип.", (List)null, false);
                                        return var10;
                                    case "update":
                                        if (!(cmd instanceof UpdateCommand)) {
                                            var10 = new CommandResponse("Ошибка: Команда update имеет неверный тип.", (List)null, false);
                                            return var10;
                                        } else {
                                            if (argument != null && !argument.trim().isEmpty()) {
                                                try {
                                                    index = Integer.parseInt(argument);
                                                    ((UpdateCommand)cmd).execute(index, band);
                                                    logger.info("Обновлена группа с ID {}: {}", index, band.getName());
                                                    var11 = new CommandResponse("Музыкальная группа успешно обновлена.", (List)null, true);
                                                    return var11;
                                                } catch (NumberFormatException var21) {
                                                    var11 = new CommandResponse("Ошибка: ID должен быть числом.", (List)null, false);
                                                    return var11;
                                                }
                                            }

                                            var10 = new CommandResponse("Ошибка: Требуется указать ID.", (List)null, false);
                                            return var10;
                                        }
                                    case "remove_lower":
                                        if (!(cmd instanceof RemoveLowerCommand)) {
                                            var10 = new CommandResponse("Ошибка: Команда remove_lower имеет неверный тип.", (List)null, false);
                                            return var10;
                                        }

                                        ((RemoveLowerCommand)cmd).execute(band);
                                        logger.info("Удалены группы ниже: {}", band.getName());
                                        var10 = new CommandResponse("Команда remove_lower выполнена.", (List)null, true);
                                        return var10;
                                    case "insert_at":
                                        if (!(cmd instanceof InsertAtCommand)) {
                                            var10 = new CommandResponse("Ошибка: Команда insert_at имеет неверный тип.", (List)null, false);
                                            return var10;
                                        } else {
                                            if (argument != null && !argument.trim().isEmpty()) {
                                                try {
                                                    index = Integer.parseInt(argument);
                                                    ((InsertAtCommand)cmd).execute(index, band);
                                                    logger.info("Вставлена группа на позицию {}: {}", index, band.getName());
                                                    var11 = new CommandResponse("Команда insert_at выполнена.", (List)null, true);
                                                    return var11;
                                                } catch (NumberFormatException var22) {
                                                    var11 = new CommandResponse("Ошибка: Индекс должен быть числом.", (List)null, false);
                                                    return var11;
                                                }
                                            }

                                            var10 = new CommandResponse("Ошибка: Требуется указать индекс.", (List)null, false);
                                            return var10;
                                        }
                                    default:
                                        var10 = new CommandResponse("Ошибка: Неизвестная команда " + command, (List)null, false);
                                        return var10;
                                }
                            }
                        }
                    }
                }
            } catch (OutOfMemoryError var26) {
                OutOfMemoryError e = var26;
                logger.error("OutOfMemoryError при обработке запроса: {}", e.getMessage());
                var3 = new CommandResponse("Ошибка: Недостаточно памяти для обработки запроса.", (List)null, false);
                return var3;
            } catch (Exception var27) {
                Exception e = var27;
                logger.error("Ошибка при обработке запроса: {}", e.getMessage());
                var3 = new CommandResponse("Ошибка: " + e.getMessage(), (List)null, false);
                return var3;
            }
        } finally {
            logger.debug("Завершение обработки запроса: {}", request.getCommandName());
        }
    }

    private boolean requiresObject(String command) {
        return command.equals("add") || command.equals("add_if_max") || command.equals("update") || command.equals("remove_lower") || command.equals("insert_at");
    }

    private void validateMusicBand(MusicBand band) {
        if (band == null) {
            throw new IllegalArgumentException("Объект MusicBand не может быть null.");
        } else if (band.getName() != null && !band.getName().isEmpty()) {
            if (band.getCoordinates() == null) {
                throw new IllegalArgumentException("Координаты не могут быть null.");
            } else if (band.getNumberOfParticipants() < 0) {
                throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
            } else if (band.getGenre() == null) {
                throw new IllegalArgumentException("Жанр не может быть null.");
            } else if (band.getBestAlbum() != null && band.getBestAlbum().getLength() > 0) {
                if (Math.abs(band.getCoordinates().getX()) > 1000000.0) {
                    throw new IllegalArgumentException("Координата X слишком большая.");
                } else if (Math.abs(band.getCoordinates().getY()) > 1000000) {
                    throw new IllegalArgumentException("Координата Y слишком большая.");
                } else if (band.getNumberOfParticipants() > 1000000) {
                    throw new IllegalArgumentException("Количество участников слишком большое.");
                } else if (band.getBestAlbum().getLength() > 1000000) {
                    throw new IllegalArgumentException("Длина альбома слишком большая.");
                }
            } else {
                throw new IllegalArgumentException("Альбом должен быть корректным с длиной больше 0.");
            }
        } else {
            throw new IllegalArgumentException("Имя группы не может быть пустым.");
        }
    }

    private String buildCommandLine(String command, String argument) {
        return argument != null ? command + " " + argument : command;
    }
}
