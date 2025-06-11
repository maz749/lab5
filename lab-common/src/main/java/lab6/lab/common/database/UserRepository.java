package lab6.lab.common.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public Long registerUser(String username, String password) throws SQLException {
        if (username == null || password == null || username.trim().isEmpty()) {
            logger.warn("Invalid registration data: username={}, password={}", username, password != null ? "****" : null);
            return null;
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String passwordHash = hashPassword(password);
                String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING id";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, passwordHash);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        Long userId = rs.getLong("id");
                        connection.commit();
                        logger.info("User registered: username={}, userId={}", username, userId);
                        return userId;
                    }
                    connection.rollback();
                    logger.warn("Failed to register user: {}", username);
                    return null;
                }
            } catch (SQLException e) {
                connection.rollback();
                if (e.getSQLState().equals("23505")) { // Unique constraint violation
                    logger.warn("User already exists: {}", username);
                    return null;
                }
                logger.error("Registration error for user {}: {}", username, e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Long authenticateUser(String username, String password) throws SQLException {
        if (username == null || password == null) {
            logger.warn("Invalid authentication data: username={}, password={}", username, password != null ? "****" : null);
            return null;
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            String passwordHash = hashPassword(password);
            String sql = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHash);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Long userId = rs.getLong("id");
                    logger.info("User authenticated: username={}, userId={}", username, userId);
                    return userId;
                }
                logger.warn("Authentication failed for user: {}", username);
                return null;
            }
        }
    }

    private String hashPassword(String password) throws SQLException {
        if (password.length() < 8) {
            throw new SQLException("Password must be at least 8 characters long.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hash).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-224 not supported: {}", e.getMessage());
            throw new SQLException("SHA-224 not supported: " + e.getMessage());
        } catch (java.io.UnsupportedEncodingException e) {
            logger.error("UTF-8 encoding error: {}", e.getMessage());
            throw new SQLException("UTF-8 encoding error: " + e.getMessage());
        }
    }
}