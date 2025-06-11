package lab6.lab.common.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseConnectionManager {
    private static final Logger logger = LogManager.getLogger(DatabaseConnectionManager.class);
    private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://pg:5432/studs";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "s465228";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "EISL2kpCTPTFhu86";
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);

    static {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL Driver registered successfully");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL Driver not found: {}", e.getMessage());
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            int connectionId = connectionCounter.incrementAndGet();
            logger.debug("New database connection established (id: {})", connectionId);
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection: {}", e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection: {}", e.getMessage());
            }
        }
    }

    public static void validateConnection(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Invalid or closed database connection");
        }
    }
}