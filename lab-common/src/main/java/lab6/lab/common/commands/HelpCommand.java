package lab6.lab.common.commands;

import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class HelpCommand implements Command {
    private static final Logger logger = LogManager.getLogger(HelpCommand.class);

    @Override
    public void execute(String argument) {
        List<String> commandList = getCommandList();
        logger.info("Executing help command, displaying {} commands.", commandList.size());
        System.out.println("Доступные команды:");
        commandList.forEach(System.out::println);
    }

    public CommandResponse executeWithResponse() {
        List<String> commandList = getCommandList();
        logger.info("Returning {} commands for help command.", commandList.size());
        return new CommandResponse("Available commands:", commandList, true);
    }

    private List<String> getCommandList() {
        return Arrays.asList(
                "add {element} - Добавить новую музыкальную группу в коллекцию.",
                "add_if_max {element} - Добавить группу, если её количество участников максимально.",
                "average_of_number_of_participants - Вычислить среднее количество участников.",
                "clear - Очистить коллекцию текущего пользователя.",
                "execute_script file_name - Выполнить скрипт из файла.",
                "filter_by_number_of_participants number - Показать группы с заданным количеством участников.",
                "filter_greater_than_number_of_participants number - Показать группы с количеством участников больше заданного.",
                "help - Показать список команд.",
                "history - Показать последние выполненные команды.",
                "info - Показать информацию о коллекции.",
                "insert_at index {element} - Вставить группу на указанную позицию.",
                "login - Войти в систему.",
                "max_by_name - Показать группу с максимальным именем.",
                "register - Зарегистрировать нового пользователя.",
                "remove_any_by_description description - Удалить одну группу с указанным описанием.",
                "remove_by_id id - Удалить группу по ID.",
                "remove_head - Удалить первую группу в коллекции.",
                "remove_lower {element} - Удалить группы с меньшим количеством участников.",
                "show - Показать все группы в коллекции.",
                "sort - Отсортировать коллекцию.",
                "update id {element} - Обновить группу с указанным ID."
        );
    }
}