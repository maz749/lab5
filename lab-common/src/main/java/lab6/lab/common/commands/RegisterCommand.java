package lab6.lab.common.commands;

import lab6.lab.common.manager.DatabaseManager;

import java.sql.SQLException;

/**
 * Команда для регистрации нового пользователя.
 */
public class RegisterCommand implements Command {
    private final DatabaseManager dbManager;

    public RegisterCommand(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда register требует логин и пароль.");
    }

    public Long execute(String username, String password) {
        try {
            Long userId = dbManager.registerUser(username, password);
            if (userId != null) {
                System.out.println("Пользователь " + username + " успешно зарегистрирован.");
                return userId;
            } else {
                System.out.println("Ошибка: Пользователь с таким именем уже существует.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при регистрации: " + e.getMessage());
            return null;
        }
    }
}