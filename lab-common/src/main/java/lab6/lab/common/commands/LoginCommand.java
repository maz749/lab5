package lab6.lab.common.commands;

import lab6.lab.common.manager.DatabaseManager;

import java.sql.SQLException;

/**
 * Команда для аутентификации пользователя.
 */
public class LoginCommand implements Command {
    private final DatabaseManager dbManager;

    public LoginCommand(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void execute(String argument) {
        System.out.println("Команда login требует логин и пароль.");
    }

    public Long execute(String username, String password) {
        try {
            Long userId = dbManager.authenticateUser(username, password);
            if (userId != null) {
                System.out.println("Вход выполнен успешно для пользователя: " + username);
                return userId;
            } else {
                System.out.println("Ошибка: Неверный логин или пароль.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при аутентификации: " + e.getMessage());
            return null;
        }
    }
}