/**
 * Команда для удаления элемента из коллекции по его id.
 */
package commands;

import manager.MusicBandManager;

public class RemoveByIdCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды RemoveByIdCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public RemoveByIdCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду удаления группы по ID.
     *
     * @param argument ID группы для удаления
     */
    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указан id для удаления.");
            }
            int id = Integer.parseInt(argument);
            if (manager.removeById(id)) {
                System.out.println("Группа с ID " + id + " удалена.");
            } else {
                System.out.println("Группа с ID " + id + " не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}