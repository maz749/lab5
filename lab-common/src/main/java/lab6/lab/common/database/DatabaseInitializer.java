package lab6.lab.common.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DatabaseInitializer {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);
    private static final Set<String> REQUIRED_TABLES = Set.of("users", "music_bands");

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            validateTables(connection);
            createTablesIfNotExist(connection);
            logger.info("Database initialization completed successfully");
        }
    }

    private static void validateTables(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        Set<String> existingTables = new HashSet<>();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                existingTables.add(tables.getString("TABLE_NAME").toLowerCase());
            }
        }

        Set<String> missingTables = new HashSet<>(REQUIRED_TABLES);
        missingTables.removeAll(existingTables);

        if (!missingTables.isEmpty()) {
            logger.warn("Missing required tables: {}", missingTables);
            throw new SQLException("Database is not properly initialized. Missing tables: " + missingTables);
        }

        logger.info("All required tables exist in the database");
    }

    private static void createTablesIfNotExist(Connection connection) throws SQLException {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(255) UNIQUE NOT NULL,
                    password_hash VARCHAR(64) NOT NULL
                );
                """;

        String createMusicBandsTable = """
                CREATE TABLE IF NOT EXISTS music_bands (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(1000) NOT NULL,
                    coordinate_x DOUBLE PRECISION NOT NULL,
                    coordinate_y INTEGER NOT NULL,
                    creation_date TIMESTAMP NOT NULL,
                    number_of_participants INTEGER NOT NULL,
                    description TEXT,
                    establishment_date DATE,
                    genre VARCHAR(50) NOT NULL,
                    best_album_name VARCHAR(255) NOT NULL,
                    best_album_length INTEGER NOT NULL,
                    user_id BIGINT NOT NULL,
                    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
                    CONSTRAINT check_number_of_participants CHECK (number_of_participants >= 0),
                    CONSTRAINT check_best_album_length CHECK (best_album_length > 0)
                );
                CREATE INDEX IF NOT EXISTS idx_music_bands_user_id ON music_bands(user_id);
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMusicBandsTable);
            logger.info("Database tables created or already exist");
        }
    }
}