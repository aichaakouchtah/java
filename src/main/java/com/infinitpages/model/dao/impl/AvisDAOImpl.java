package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.AvisDAO;
import com.infinitpages.model.entity.Avis;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de AvisDAO.
 */
public class AvisDAOImpl implements AvisDAO {
    
    @Override
    public Optional<Avis> findById(int id) {
        String sql = "SELECT * FROM avis WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAvis(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'avis par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Avis> findAll() {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis ORDER BY date_avis DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                avis.add(mapResultSetToAvis(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les avis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return avis;
    }
    
    @Override
    public List<Avis> findByDocument(int idDocument) {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE id_document = ? ORDER BY date_avis DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avis.add(mapResultSetToAvis(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return avis;
    }
    
    @Override
    public List<Avis> findModeresByDocument(int idDocument) {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE id_document = ? AND est_modere = TRUE ORDER BY date_avis DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avis.add(mapResultSetToAvis(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des avis modérés: " + e.getMessage());
            e.printStackTrace();
        }
        
        return avis;
    }
    
    @Override
    public List<Avis> findNonModeres() {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE est_modere = FALSE ORDER BY date_avis ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                avis.add(mapResultSetToAvis(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des avis non modérés: " + e.getMessage());
            e.printStackTrace();
        }
        
        return avis;
    }
    
    @Override
    public List<Avis> findByUtilisateur(int idUtilisateur) {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE id_utilisateur = ? ORDER BY date_avis DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avis.add(mapResultSetToAvis(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return avis;
    }
    
    @Override
    public Optional<Avis> findByUtilisateurAndDocument(int idUtilisateur, int idDocument) {
        String sql = "SELECT * FROM avis WHERE id_utilisateur = ? AND id_document = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAvis(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par utilisateur et document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public double calculerNoteGlobale(int idDocument) {
        String sql = "SELECT AVG(note) FROM avis WHERE id_document = ? AND est_modere = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double moyenne = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : moyenne;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la note globale: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    @Override
    public int countByDocument(int idDocument) {
        String sql = "SELECT COUNT(*) FROM avis WHERE id_document = ? AND est_modere = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des avis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public Avis save(Avis avis) {
        String sql = "INSERT INTO avis (id_utilisateur, id_document, contenu, note, date_avis, est_modere) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, avis.getUtilisateur().getId());
            stmt.setInt(2, avis.getDocument().getId());
            stmt.setString(3, avis.getContenu());
            stmt.setInt(4, avis.getNote());
            
            if (avis.getDateAvis() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(avis.getDateAvis()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }
            
            stmt.setBoolean(6, avis.isEstModere());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avis.setId(generatedKeys.getInt(1));
                        return avis;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de l'avis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Avis avis) {
        String sql = "UPDATE avis SET id_utilisateur = ?, id_document = ?, contenu = ?, note = ?, " +
                     "date_avis = ?, est_modere = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, avis.getUtilisateur().getId());
            stmt.setInt(2, avis.getDocument().getId());
            stmt.setString(3, avis.getContenu());
            stmt.setInt(4, avis.getNote());
            stmt.setTimestamp(5, Timestamp.valueOf(avis.getDateAvis()));
            stmt.setBoolean(6, avis.isEstModere());
            stmt.setInt(7, avis.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'avis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM avis WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'avis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Avis.
     */
    private Avis mapResultSetToAvis(ResultSet rs) throws SQLException {
        Avis avis = new Avis();
        
        avis.setId(rs.getInt("id"));
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id_utilisateur"));
        avis.setUtilisateur(utilisateur);
        
        Document document = new Document() {};
        document.setId(rs.getInt("id_document"));
        avis.setDocument(document);
        
        avis.setContenu(rs.getString("contenu"));
        avis.setNote(rs.getInt("note"));
        
        Timestamp dateAvis = rs.getTimestamp("date_avis");
        if (dateAvis != null) {
            avis.setDateAvis(dateAvis.toLocalDateTime());
        }
        
        avis.setEstModere(rs.getBoolean("est_modere"));
        
        return avis;
    }
}

