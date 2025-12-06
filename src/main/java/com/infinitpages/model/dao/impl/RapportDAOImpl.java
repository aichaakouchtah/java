package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.RapportDAO;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Rapport;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de RapportDAO.
 */
public class RapportDAOImpl implements RapportDAO {
    
    @Override
    public Optional<Rapport> findById(int id) {
        String sql = "SELECT * FROM rapport WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de rapport par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Rapport> findAll() {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport ORDER BY date_generation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rapports.add(mapResultSetToRapport(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les rapports: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapports;
    }
    
    @Override
    public List<Rapport> findByAdmin(int idAdmin) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE id_admin = ? ORDER BY date_generation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAdmin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapports;
    }
    
    @Override
    public List<Rapport> findByType(String type) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE type = ? ORDER BY date_generation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapports;
    }
    
    @Override
    public List<Rapport> findByPeriode(String periode) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE periode = ? ORDER BY date_generation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, periode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par période: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapports;
    }
    
    @Override
    public List<Rapport> findByAdminAndType(int idAdmin, String type) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE id_admin = ? AND type = ? ORDER BY date_generation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAdmin);
            stmt.setString(2, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par admin et type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rapports;
    }
    
    @Override
    public Rapport save(Rapport rapport) {
        String sql = "INSERT INTO rapport (id_admin, titre, date_generation, contenu, type, periode) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, rapport.getAdmin().getId());
            stmt.setString(2, rapport.getTitre());
            
            if (rapport.getDateGeneration() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(rapport.getDateGeneration()));
            } else {
                stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }
            
            stmt.setString(4, rapport.getContenu());
            stmt.setString(5, rapport.getType());
            stmt.setString(6, rapport.getPeriode());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rapport.setId(generatedKeys.getInt(1));
                        return rapport;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du rapport: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Rapport rapport) {
        String sql = "UPDATE rapport SET id_admin = ?, titre = ?, date_generation = ?, " +
                     "contenu = ?, type = ?, periode = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rapport.getAdmin().getId());
            stmt.setString(2, rapport.getTitre());
            stmt.setTimestamp(3, Timestamp.valueOf(rapport.getDateGeneration()));
            stmt.setString(4, rapport.getContenu());
            stmt.setString(5, rapport.getType());
            stmt.setString(6, rapport.getPeriode());
            stmt.setInt(7, rapport.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du rapport: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM rapport WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du rapport: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Rapport.
     */
    private Rapport mapResultSetToRapport(ResultSet rs) throws SQLException {
        Rapport rapport = new Rapport();
        
        rapport.setId(rs.getInt("id"));
        
        Admin admin = new Admin();
        admin.setId(rs.getInt("id_admin"));
        rapport.setAdmin(admin);
        
        rapport.setTitre(rs.getString("titre"));
        
        Timestamp dateGeneration = rs.getTimestamp("date_generation");
        if (dateGeneration != null) {
            rapport.setDateGeneration(dateGeneration.toLocalDateTime());
        }
        
        rapport.setContenu(rs.getString("contenu"));
        rapport.setType(rs.getString("type"));
        rapport.setPeriode(rs.getString("periode"));
        
        return rapport;
    }
}

