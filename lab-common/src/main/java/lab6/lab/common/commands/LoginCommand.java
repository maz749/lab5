package lab6.lab.common.commands;

import lab6.lab.common.database.UserRepository;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Команда для аутентификации пользователя.
 */
public class LoginCommand implements Command {
    private static final Logger logger = LogManager.getLogger(LoginCommand.class);
    private final UserRepository userRepository;

    public LoginCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        logger.info("Login command requires username and password");
        System.out.println("Команда login требует логин и пароль.");
    }

    public CommandResponse executeWithResponse(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is required for login");
            throw new IllegalArgumentException("Username is required for login");
        }
        if (password == null || password.trim().isEmpty()) {
            logger.error("Password is required for login");
            throw new IllegalArgumentException("Password is required for login");
        }

        try {
            Long userId = userRepository.authenticateUser(username, password);
            if (userId != null) {
                logger.info("User {} successfully logged in with id {}", username, userId);
                return new CommandResponse("User " + username + " successfully logged in.", null, true);
            } else {
                logger.warn("Login failed: invalid credentials for user {}", username);
                return new CommandResponse("Error: Invalid username or password.", null, false);
            }
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            throw new RuntimeException("Error during login: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "login to the system";
    }
}