package lab6.lab.common.database;

import lab6.lab.common.models.MusicBand;
import lab6.lab.common.models.MusicGenre;
import lab6.lab.common.models.Coordinates;
import lab6.lab.common.models.Album;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MusicBandRepository {
    private static final Logger logger = LogManager.getLogger(MusicBandRepository.class);

    public List<MusicBand> getAllBands() throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            String sql = "SELECT * FROM music_bands";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<MusicBand> bands = new ArrayList<>();
                while (rs.next()) {
                    bands.add(mapResultSetToMusicBand(rs));
                }
                logger.info("Retrieved {} bands from database", bands.size());
                return bands;
            }
        }
    }

    public List<MusicBand> getBandsByUserId(Long userId) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            String sql = "SELECT * FROM music_bands WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<MusicBand> bands = new ArrayList<>();
                    while (rs.next()) {
                        bands.add(mapResultSetToMusicBand(rs));
                    }
                    logger.info("Retrieved {} bands for userId: {}", bands.size(), userId);
                    return bands;
                }
            }
        }
    }

    public MusicBand addBand(MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            throw new SQLException("Cannot add null MusicBand");
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = """
                    INSERT INTO music_bands (name, coordinate_x, coordinate_y, creation_date, number_of_participants,
                        description, establishment_date, genre, best_album_name, best_album_length, user_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """;
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    setBandParameters(stmt, band, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        band.setId(rs.getInt("id"));
                        band.setUserId(userId);
                        connection.commit();
                        logger.info("Added band {} to database, id: {}, userId: {}", band.getName(), band.getId(), userId);
                        return band;
                    }
                    connection.rollback();
                    throw new SQLException("Failed to add band to database");
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error adding band {}: {}", band.getName(), e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateBand(int id, MusicBand band, Long userId) throws SQLException {
        if (band == null) {
            throw new SQLException("Cannot update with null MusicBand");
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = """
                    UPDATE music_bands SET name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?,
                        number_of_participants = ?, description = ?, establishment_date = ?, genre = ?,
                        best_album_name = ?, best_album_length = ?
                    WHERE id = ? AND user_id = ? RETURNING id
                    """;
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    setBandParameters(stmt, band, userId);
                    stmt.setInt(11, id);
                    stmt.setLong(12, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        band.setId(id);
                        band.setUserId(userId);
                        connection.commit();
                        logger.info("Updated band id {} in database, userId: {}", id, userId);
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
    }

    public boolean deleteBand(int id, Long userId) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "DELETE FROM music_bands WHERE id = ? AND user_id = ? RETURNING id";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.setLong(2, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        connection.commit();
                        logger.info("Deleted band id {} from database, userId: {}", id, userId);
                        return true;
                    }
                    connection.rollback();
                    logger.warn("Band id {} not found or not owned by userId: {}", id, userId);
                    return false;
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error deleting band id {}: {}", id, e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void deleteBandsByUserId(Long userId) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "DELETE FROM music_bands WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, userId);
                    int rowsAffected = stmt.executeUpdate();
                    connection.commit();
                    logger.info("Deleted {} bands for userId: {}", rowsAffected, userId);
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error deleting bands for userId {}: {}", userId, e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void deleteLowerBands(int numberOfParticipants, Long userId) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "DELETE FROM music_bands WHERE number_of_participants < ? AND user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, numberOfParticipants);
                    stmt.setLong(2, userId);
                    int rowsAffected = stmt.executeUpdate();
                    connection.commit();
                    logger.info("Deleted {} bands with fewer than {} participants for userId: {}",
                            rowsAffected, numberOfParticipants, userId);
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error deleting lower bands for userId {}: {}", userId, e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void setBandParameters(PreparedStatement stmt, MusicBand band, Long userId) throws SQLException {
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
    }

    private MusicBand mapResultSetToMusicBand(ResultSet rs) throws SQLException {
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
        return band;
    }
}