package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.NotificationDAO;
import com.infinitpages.model.entity.Notification;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de NotificationDAO.
 */
public class NotificationDAOImpl implements NotificationDAO {
    
    @Override
    public Optional<Notification> findById(int id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de notification par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Notification> findByDestinataire(int idDestinataire) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE id_destinataire = ? ORDER BY date_envoi DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestinataire);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par destinataire: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> findNonLuesByDestinataire(int idDestinataire) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE id_destinataire = ? AND est_lue = FALSE ORDER BY date_envoi DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestinataire);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des notifications non lues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> findByType(String type) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE type = ? ORDER BY date_envoi DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> findNonLues() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE est_lue = FALSE ORDER BY date_envoi DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des notifications non lues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    @Override
    public int countNonLues(int idDestinataire) {
        String sql = "SELECT COUNT(*) FROM notification WHERE id_destinataire = ? AND est_lue = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestinataire);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des notifications non lues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean marquerCommeLue(int id) {
        String sql = "UPDATE notification SET est_lue = TRUE, date_lecture = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage comme lue: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int marquerToutesCommeLues(int idDestinataire) {
        String sql = "UPDATE notification SET est_lue = TRUE, date_lecture = ? WHERE id_destinataire = ? AND est_lue = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(2, idDestinataire);
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage de toutes comme lues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public Notification save(Notification notification) {
        String sql = "INSERT INTO notification (id_destinataire, message, date_envoi, date_lecture, " +
                     "est_lue, type, priorite) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, notification.getDestinataire().getId());
            stmt.setString(2, notification.getMessage());
            
            if (notification.getDateEnvoi() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(notification.getDateEnvoi()));
            } else {
                stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }
            
            if (notification.getDateLecture() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(notification.getDateLecture()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setBoolean(5, notification.isEstLue());
            stmt.setString(6, notification.getType());
            stmt.setString(7, notification.getPriorite());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        notification.setId(generatedKeys.getInt(1));
                        return notification;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de la notification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Notification notification) {
        String sql = "UPDATE notification SET id_destinataire = ?, message = ?, date_envoi = ?, " +
                     "date_lecture = ?, est_lue = ?, type = ?, priorite = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notification.getDestinataire().getId());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getDateEnvoi()));
            
            if (notification.getDateLecture() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(notification.getDateLecture()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setBoolean(5, notification.isEstLue());
            stmt.setString(6, notification.getType());
            stmt.setString(7, notification.getPriorite());
            stmt.setInt(8, notification.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la notification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la notification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int deleteLuesByDestinataire(int idDestinataire) {
        String sql = "DELETE FROM notification WHERE id_destinataire = ? AND est_lue = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestinataire);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des notifications lues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers un objet Notification.
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        
        notification.setId(rs.getInt("id"));
        
        Personne destinataire = new Personne() {};
        destinataire.setId(rs.getInt("id_destinataire"));
        notification.setDestinataire(destinataire);
        
        notification.setMessage(rs.getString("message"));
        
        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        if (dateEnvoi != null) {
            notification.setDateEnvoi(dateEnvoi.toLocalDateTime());
        }
        
        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            notification.setDateLecture(dateLecture.toLocalDateTime());
        }
        
        notification.setEstLue(rs.getBoolean("est_lue"));
        notification.setType(rs.getString("type"));
        notification.setPriorite(rs.getString("priorite"));
        
        return notification;
    }
}

