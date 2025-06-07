package lab6.lab.common.manager;

import lab6.lab.common.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

public class DatabaseManager implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://pg:5432/studs";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "s465228";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "EISL2kpCTPTFhu86";
    private final Connection connection;
    private final MusicBandCollection collection;

    public DatabaseManager(MusicBandCollection collection) throws SQLException {
        this.collection = collection;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL Driver not found: {}", e.getMessage());
            throw new SQLException("PostgreSQL Driver not found", e);
        }
        this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        logger.info("Connected to database: {}", DB_URL);
        initializeDatabase();
        loadCollection();
    }

    private void initializeDatabase() throws SQLException {
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
            logger.info("Database tables initialized.");
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

    public Long registerUser(String username, String password) throws SQLException {
        if (username == null || password == null || username.trim().isEmpty()) {
            logger.warn("Invalid registration data: username={}, password={}", username, password != null ? "****" : null);
            return null;
        }
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
                return null; // User already exists
            }
            logger.error("Registration error for user {}: {}", username, e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Long authenticateUser(String username, String password) throws SQLException {
        if (username == null || password == null) {
            logger.warn("Invalid authentication data: username={}, password={}", username, password != null ? "****" : null);
            return null;
        }
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

    void loadCollection() throws SQLException {
        String sql = "SELECT * FROM music_bands";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            collection.clear();
            int count = 0;
            while (rs.next()) {
                MusicBand band = new MusicBand();
                band.setId(rs.getInt("id"));
                band.setName(rs.getString("name"));
                band.setCoordinates(new Coordinates(rs.getDouble("coordinate_x"), rs.getInt("coordinate_y")));
                band.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                band.setNumberOfParticipants(rs.getInt("number_of_participants"));
                band.setDescription(rs.getString("description"));
                Date estDate = rs.getDate("establishment_date");
                if (estDate != null) {
                    band.setEstablishmentDate(estDate);
                }
                band.setGenre(MusicGenre.valueOf(rs.getString("genre")));
                band.setBestAlbum(new Album(rs.getString("best_album_name"), rs.getInt("best_album_length")));
                band.setUserId(rs.getLong("user_id"));
                collection.add(band);
                logger.debug("Loaded band: id={}, name={}, user_id={}", band.getId(), band.getName(), band.getUserId());
                count++;
            }
            logger.info("Loaded {} music bands from database into memory.", count);
            if (count == 0) {
                logger.warn("No bands loaded from database, collection is empty.");
            }
        } catch (SQLException e) {
            logger.error("Error loading collection: {}", e.getMessage());
            throw e;
        }
    }

    public void addMusicBand(MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            logger.error("Attempted to add null MusicBand to database.");
            throw new SQLException("Cannot add null MusicBand.");
        }
        connection.setAutoCommit(false);
        try {
            String sql = """
                INSERT INTO music_bands (name, coordinate_x, coordinate_y, creation_date, number_of_participants,
                    description, establishment_date, genre, best_album_name, best_album_length, user_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                """;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, band.getName());
                stmt.setDouble(2, band.getCoordinates().getX());
                stmt.setInt(3, band.getCoordinates().getY());
                stmt.setTimestamp(4, Timestamp.valueOf(band.getCreationDate()));
                stmt.setInt(5, band.getNumberOfParticipants());
                stmt.setString(6, band.getDescription());
                stmt.setDate(7, band.getEstablishmentDate() != null ?
                        Date.valueOf(LocalDate.from(band.getEstablishmentDate().toInstant().atZone(java.time.ZoneId.systemDefault()))) : null);
                stmt.setString(8, band.getGenre().name());
                stmt.setString(9, band.getBestAlbum().getName());
                stmt.setInt(10, band.getBestAlbum().getLength());
                stmt.setLong(11, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    band.setId(rs.getInt("id"));
                    band.setUserId(userId);
                    int initialSize = collection.size();
                    collection.add(band);
                    if (collection.size() == initialSize + 1) {
                        logger.info("Successfully added band {} to in-memory collection, new size: {}", band.getName(), collection.size());
                        connection.commit();
                        logger.info("Successfully committed band {} to database, id: {}, userId: {}", band.getName(), band.getId(), userId);
                    } else {
                        logger.error("Failed to add band {} to in-memory collection, size unchanged: {}", band.getName(), collection.size());
                        connection.rollback();
                        throw new SQLException("Failed to add band to in-memory collection.");
                    }
                } else {
                    connection.rollback();
                    logger.warn("Failed to add band {} to database.", band.getName());
                    throw new SQLException("Failed to add band to database.");
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error adding band {}: {}", band.getName(), e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean addIfMax(MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            logger.error("Attempted to add null MusicBand for addIfMax.");
            return false;
        }
        boolean added = collection.addIfMax(band);
        if (added) {
            connection.setAutoCommit(false);
            try {
                String sql = """
                    INSERT INTO music_bands (name, coordinate_x, coordinate_y, creation_date, number_of_participants,
                        description, establishment_date, genre, best_album_name, best_album_length, user_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """;
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, band.getName());
                    stmt.setDouble(2, band.getCoordinates().getX());
                    stmt.setInt(3, band.getCoordinates().getY());
                    stmt.setTimestamp(4, Timestamp.valueOf(band.getCreationDate()));
                    stmt.setInt(5, band.getNumberOfParticipants());
                    stmt.setString(6, band.getDescription());
                    stmt.setDate(7, band.getEstablishmentDate() != null ?
                            Date.valueOf(LocalDate.from(band.getEstablishmentDate().toInstant().atZone(java.time.ZoneId.systemDefault()))) : null);
                    stmt.setString(8, band.getGenre().name());
                    stmt.setString(9, band.getBestAlbum().getName());
                    stmt.setInt(10, band.getBestAlbum().getLength());
                    stmt.setLong(11, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        band.setId(rs.getInt("id"));
                        band.setUserId(userId);
                        connection.commit();
                        logger.info("Band {} added as max to database, id: {}, userId: {}", band.getName(), band.getId(), userId);
                    } else {
                        connection.rollback();
                        collection.removeById(band.getId());
                        logger.warn("Failed to add band {} to database, removed from collection.", band.getName());
                        return false;
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                collection.removeById(band.getId());
                logger.error("Error adding band {} as max: {}", band.getName(), e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        logger.debug("AddIfMax result for band {}: added={}", band.getName(), added);
        return added;
    }

    public boolean updateMusicBand(int id, MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            logger.error("Attempted to update with null MusicBand for id: {}", id);
            return false;
        }
        connection.setAutoCommit(false);
        try {
            String sql = """
                UPDATE music_bands SET name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?,
                    number_of_participants = ?, description = ?, establishment_date = ?, genre = ?,
                    best_album_name = ?, best_album_length = ?
                WHERE id = ? AND user_id = ? RETURNING id
                """;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, band.getName());
                stmt.setDouble(2, band.getCoordinates().getX());
                stmt.setInt(3, band.getCoordinates().getY());
                stmt.setTimestamp(4, Timestamp.valueOf(band.getCreationDate()));
                stmt.setInt(5, band.getNumberOfParticipants());
                stmt.setString(6, band.getDescription());
                stmt.setDate(7, band.getEstablishmentDate() != null ?
                        Date.valueOf(LocalDate.from(band.getEstablishmentDate().toInstant().atZone(java.time.ZoneId.systemDefault()))) : null);
                stmt.setString(8, band.getGenre().name());
                stmt.setString(9, band.getBestAlbum().getName());
                stmt.setInt(10, band.getBestAlbum().getLength());
                stmt.setInt(11, id);
                stmt.setLong(12, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    band.setId(id);
                    band.setUserId(userId);
                    collection.update(id, band);
                    connection.commit();
                    logger.info("Updated band id {} in database and collection, userId: {}", id, userId);
                    return true;
                }
                connection.rollback();
                logger.warn("Band id {} not found or not owned by userId: {}", id, userId);
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error updating band id {}: {}", id, e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean removeMusicBand(int id, Long userId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sql = "DELETE FROM music_bands WHERE id = ? AND user_id = ? RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setLong(2, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    collection.removeById(id);
                    connection.commit();
                    logger.info("Removed band id {} from database and collection, userId: {}", id, userId);
                    return true;
                }
                connection.rollback();
                logger.warn("Band id {} not found or not owned by userId: {}", id, userId);
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error removing band id {}: {}", id, e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void clear(Long userId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sql = "DELETE FROM music_bands";
            try (Statement stmt = connection.createStatement()) {
                int rowsAffected = stmt.executeUpdate(sql);
                synchronized (collection.getMusicBands()) {
                    collection.getMusicBands().clear();
                    logger.info("Cleared in-memory collection, new size: {}", collection.size());
                }
                connection.commit();
                logger.info("Cleared all {} records from music_bands table in database.", rowsAffected);
            }
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error clearing music_bands table: {}", e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void insertAtIndex(int index, MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            logger.error("Attempted to insert null MusicBand at index {}.", index);
            throw new SQLException("Cannot insert null MusicBand.");
        }
        connection.setAutoCommit(false);
        try {
            addMusicBand(band, userId);
            synchronized (collection.getMusicBands()) {
                List<MusicBand> bands = collection.getMusicBands();
                bands.removeIf(b -> b.getId() == band.getId());
                if (index >= 0 && index <= bands.size()) {
                    bands.add(index, band);
                } else {
                    bands.add(band);
                }
                logger.info("Inserted band {} at index {} in collection, userId: {}", band.getName(), index, userId);
            }
            connection.commit();
            logger.info("Inserted band {} at index {} in database, userId: {}", band.getName(), index, userId);
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error inserting band {} at index {}: {}", band.getName(), index, e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void removeLower(MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            logger.error("Attempted to remove lower with null MusicBand.");
            throw new SQLException("Cannot remove lower with null MusicBand.");
        }
        connection.setAutoCommit(false);
        try {
            String sql = "DELETE FROM music_bands WHERE number_of_participants < ? AND user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, band.getNumberOfParticipants());
                stmt.setLong(2, userId);
                int rowsAffected = stmt.executeUpdate();
                synchronized (collection.getMusicBands()) {
                    collection.getMusicBands().removeIf(b -> b.getNumberOfParticipants() < band.getNumberOfParticipants() && b.getUserId() == userId);
                    logger.info("Removed {} bands with fewer participants than {} from collection, userId: {}", rowsAffected, band.getNumberOfParticipants(), userId);
                }
                connection.commit();
                logger.info("Removed {} bands with fewer participants than {} from database, userId: {}", rowsAffected, band.getNumberOfParticipants(), userId);
            }
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error removing lower bands for userId {}: {}", userId, e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Database connection closed.");
        }
    }
}