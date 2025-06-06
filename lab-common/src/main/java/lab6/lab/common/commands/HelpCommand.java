package lab6.lab.common.commands;

public class HelpCommand implements lab6.lab.common.commands.Command {
    @Override
    public void execute(String argument) {
        System.out.println("Доступные команды:");
        System.out.println("help : вывести справку по доступным командам");
        System.out.println("info : вывести информацию о коллекции");
        System.out.println("show : вывести все элементы коллекции");
        System.out.println("add {element} : добавить новый элемент в коллекцию");
        System.out.println("update id {element} : обновить значение элемента по id");
        System.out.println("remove_by_id id : удалить элемент по id");
        System.out.println("clear : очистить коллекцию");
        System.out.println("execute_script file_name : выполнить скрипт из файла");
        System.out.println("exit : завершить программу");
        System.out.println("insert_at index {element} : добавить элемент на указанную позицию");
        System.out.println("sort : отсортировать коллекцию по имени");
        System.out.println("history : вывести последние 12 команд");
        System.out.println("average_of_number_of_participants : вывести среднее количество участников");
        System.out.println("filter_greater_than_number_of_participants numberOfParticipants : вывести элементы с количеством участников больше заданного");
        System.out.println("max_by_name : вывести группу с максимальным именем");
        System.out.println("add_if_max : добавить, если максимальное");
        System.out.println("remove_lower : удалить элементы с меньшим количеством участников");
        System.out.println("remove_any_by_description : удалить по описанию");
    }
}