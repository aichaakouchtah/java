package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.HistoriqueDAO;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Historique;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de HistoriqueDAO.
 */
public class HistoriqueDAOImpl implements HistoriqueDAO {
    
    @Override
    public Optional<Historique> findById(int id) {
        String sql = "SELECT * FROM historique WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHistorique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'historique par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Historique> findByPersonne(int idPersonne) {
        List<Historique> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historique WHERE id_personne = ? ORDER BY date_creation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPersonne);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiques.add(mapResultSetToHistorique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par personne: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historiques;
    }
    
    @Override
    public List<Historique> findByType(String type) {
        List<Historique> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historique WHERE type = ? ORDER BY date_creation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiques.add(mapResultSetToHistorique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historiques;
    }
    
    @Override
    public List<Historique> findByPersonneAndType(int idPersonne, String type) {
        List<Historique> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historique WHERE id_personne = ? AND type = ? ORDER BY date_creation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPersonne);
            stmt.setString(2, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiques.add(mapResultSetToHistorique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par personne et type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historiques;
    }
    
    @Override
    public List<Historique> findListesPartagees() {
        List<Historique> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historique WHERE est_partage = TRUE ORDER BY date_creation DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                historiques.add(mapResultSetToHistorique(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des listes partagées: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historiques;
    }
    
    @Override
    public List<Document> findDocumentsByHistorique(int idHistorique) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.* FROM document d " +
                     "INNER JOIN historique_document hd ON d.id = hd.id_document " +
                     "WHERE hd.id_historique = ? " +
                     "ORDER BY hd.date_ajout DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHistorique);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Document doc = new Document() {};
                    doc.setId(rs.getInt("id"));
                    doc.setTitre(rs.getString("titre"));
                    doc.setAuteur(rs.getString("auteur"));
                    documents.add(doc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public boolean addDocument(int idHistorique, int idDocument) {
        String sql = "INSERT INTO historique_document (id_historique, id_document) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHistorique);
            stmt.setInt(2, idDocument);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean removeDocument(int idHistorique, int idDocument) {
        String sql = "DELETE FROM historique_document WHERE id_historique = ? AND id_document = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHistorique);
            stmt.setInt(2, idDocument);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du retrait du document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public Historique save(Historique historique) {
        String sql = "INSERT INTO historique (id_personne, type, titre, description, " +
                     "date_creation, date_modification, est_partage, details) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, historique.getUtilisateur().getId());
            stmt.setString(2, historique.getType());
            stmt.setString(3, historique.getTitre());
            stmt.setString(4, historique.getDescription());
            
            if (historique.getDateCreation() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(historique.getDateCreation()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }
            
            if (historique.getDateModification() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(historique.getDateModification()));
            } else {
                stmt.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }
            
            stmt.setBoolean(7, historique.isEstPartage());
            stmt.setString(8, historique.getDetails());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        historique.setId(generatedKeys.getInt(1));
                        return historique;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de l'historique: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Historique historique) {
        String sql = "UPDATE historique SET id_personne = ?, type = ?, titre = ?, description = ?, " +
                     "date_modification = ?, est_partage = ?, details = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historique.getUtilisateur().getId());
            stmt.setString(2, historique.getType());
            stmt.setString(3, historique.getTitre());
            stmt.setString(4, historique.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setBoolean(6, historique.isEstPartage());
            stmt.setString(7, historique.getDetails());
            stmt.setInt(8, historique.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'historique: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM historique WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'historique: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Historique.
     */
    private Historique mapResultSetToHistorique(ResultSet rs) throws SQLException {
        Historique historique = new Historique();
        
        historique.setId(rs.getInt("id"));
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id_personne"));
        historique.setUtilisateur(utilisateur);
        
        historique.setType(rs.getString("type"));
        historique.setTitre(rs.getString("titre"));
        historique.setDescription(rs.getString("description"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            historique.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            historique.setDateModification(dateModification.toLocalDateTime());
        }
        
        historique.setEstPartage(rs.getBoolean("est_partage"));
        historique.setDetails(rs.getString("details"));
        
        return historique;
    }
}

