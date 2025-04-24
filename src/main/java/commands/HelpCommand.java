/**
 * Команда для вывода справки по доступным командам.
 */
package commands;

public class HelpCommand implements Command {
    /**
     * Выполняет команду вывода справки.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        System.out.println("Доступные команды:");
        System.out.println("help : вывести справку по доступным командам");
        System.out.println("info : вывести информацию о коллекции");
        System.out.println("show : вывести все элементы коллекции");
        System.out.println("add {element} : добавить новый элемент в коллекцию");
        System.out.println("update id {element} : обновить значение элемента коллекции, id которого равен заданному");
        System.out.println("remove_by_id id : удалить элемент из коллекции по его id");
        System.out.println("clear : очистить коллекцию");
        System.out.println("save : сохранить коллекцию в файл");
        System.out.println("execute_script file_name : считать и исполнить скрипт из указанного файла");
        System.out.println("exit : завершить программу (без сохранения в файл)");
        System.out.println("insert_at index {element} : добавить новый элемент в заданную позицию");
        System.out.println("sort : отсортировать коллекцию в естественном порядке");
        System.out.println("history : вывести последние 12 команд (без их аргументов)");
        System.out.println("average_of_number_of_participants : вывести среднее значение поля numberOfParticipants");
        System.out.println("filter_greater_than_number_of_participants numberOfParticipants : вывести элементы, значение поля numberOfParticipants которых больше заданного");
        System.out.println("print_field_descending_number_of_participants : вывести значения поля numberOfParticipants в порядке убывания");
        System.out.println("remove_head :");
        System.out.println("help : вывести справку по доступным командам");
        System.out.println("max_by_name : вывести группу с максимальным именем");
        System.out.println("add_if_max : добавить, если максимальное");
        System.out.println("remove_lower : ");
        System.out.println("remove_any_by_desription : удалить по описанию");
    }
}