package lab6.lab.common.commands;

import lab6.lab.common.database.UserRepository;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Команда для регистрации нового пользователя.
 */
public class RegisterCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RegisterCommand.class);
    private final UserRepository userRepository;

    public RegisterCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(String argument, Object object) {
        logger.info("Register command requires username and password");
        System.out.println("Команда register требует логин и пароль.");
    }

    public CommandResponse executeWithResponse(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is required for registration");
            throw new IllegalArgumentException("Username is required for registration");
        }
        if (password == null || password.trim().isEmpty()) {
            logger.error("Password is required for registration");
            throw new IllegalArgumentException("Password is required for registration");
        }

        try {
            Long userId = userRepository.registerUser(username, password);
            if (userId != null) {
                logger.info("User {} successfully registered with id {}", username, userId);
                return new CommandResponse("User " + username + " successfully registered.", null, true);
            } else {
                logger.warn("Registration failed: user {} already exists", username);
                return new CommandResponse("Error: User " + username + " already exists.", null, false);
            }
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage());
            throw new RuntimeException("Error during registration: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "register a new user";
    }
}