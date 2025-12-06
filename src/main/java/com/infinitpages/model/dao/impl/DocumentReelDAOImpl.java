package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.DocumentReelDAO;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.DocumentReel;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de DocumentReelDAO.
 */
public class DocumentReelDAOImpl extends DocumentDAOImpl implements DocumentReelDAO {
    
    // Implémentation de DocumentDAO.findById pour DocumentReel
    @Override
    public Optional<Document> findById(int id) {
        Optional<DocumentReel> documentReel = findDocumentReelById(id);
        return documentReel.map(dr -> (Document) dr);
    }
    
    // Méthode spécifique pour DocumentReel (utilisée en interne)
    private Optional<DocumentReel> findDocumentReelById(int id) {
        String sql = "SELECT d.*, dr.emplacement, dr.condition_livre, dr.isbn " +
                     "FROM document d " +
                     "INNER JOIN document_reel dr ON d.id = dr.id_document " +
                     "WHERE d.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocumentReel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de document réel par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Implémentation de DocumentDAO.findAll pour DocumentReel
    @Override
    public List<Document> findAll() {
        List<DocumentReel> documentsReels = findAllDocumentReels();
        List<Document> documents = new ArrayList<>();
        for (DocumentReel dr : documentsReels) {
            documents.add((Document) dr);
        }
        return documents;
    }
    
    // Méthode spécifique pour DocumentReel (utilisée en interne)
    private List<DocumentReel> findAllDocumentReels() {
        List<DocumentReel> documents = new ArrayList<>();
        String sql = "SELECT d.*, dr.emplacement, dr.condition_livre, dr.isbn " +
                     "FROM document d " +
                     "INNER JOIN document_reel dr ON d.id = dr.id_document " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocumentReel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les documents réels: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public Optional<DocumentReel> findByISBN(String isbn) {
        String sql = "SELECT d.*, dr.emplacement, dr.condition_livre, dr.isbn " +
                     "FROM document d " +
                     "INNER JOIN document_reel dr ON d.id = dr.id_document " +
                     "WHERE dr.isbn = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocumentReel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par ISBN: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<DocumentReel> findByEmplacement(String emplacement) {
        List<DocumentReel> documents = new ArrayList<>();
        String sql = "SELECT d.*, dr.emplacement, dr.condition_livre, dr.isbn " +
                     "FROM document d " +
                     "INNER JOIN document_reel dr ON d.id = dr.id_document " +
                     "WHERE dr.emplacement = ? " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, emplacement);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocumentReel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par emplacement: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<DocumentReel> findByCondition(String condition) {
        List<DocumentReel> documents = new ArrayList<>();
        String sql = "SELECT d.*, dr.emplacement, dr.condition_livre, dr.isbn " +
                     "FROM document d " +
                     "INNER JOIN document_reel dr ON d.id = dr.id_document " +
                     "WHERE dr.condition_livre = ? " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, condition);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocumentReel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par condition: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public DocumentReel save(DocumentReel documentReel) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. D'abord sauvegarder le document de base
            DocumentReel docBase = (DocumentReel) super.save(documentReel);
            
            if (docBase != null && docBase.getId() > 0) {
                // 2. Ensuite insérer dans document_reel
                String sqlReel = "INSERT INTO document_reel (id_document, emplacement, condition_livre, isbn) " +
                                "VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement stmtReel = conn.prepareStatement(sqlReel)) {
                    stmtReel.setInt(1, docBase.getId());
                    stmtReel.setString(2, documentReel.getEmplacement());
                    stmtReel.setString(3, documentReel.getCondition());
                    stmtReel.setString(4, documentReel.getISBN());
                    
                    stmtReel.executeUpdate();
                }
                
                conn.commit();
                return docBase;
            }
            
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la sauvegarde du document réel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                }
            }
        }
        
        return null;
    }
    
    @Override
    public boolean update(DocumentReel documentReel) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Mettre à jour le document de base
            boolean docUpdated = super.update(documentReel);
            
            if (docUpdated) {
                // 2. Mettre à jour document_reel
                String sqlReel = "UPDATE document_reel SET emplacement = ?, condition_livre = ?, isbn = ? " +
                                "WHERE id_document = ?";
                
                try (PreparedStatement stmtReel = conn.prepareStatement(sqlReel)) {
                    stmtReel.setString(1, documentReel.getEmplacement());
                    stmtReel.setString(2, documentReel.getCondition());
                    stmtReel.setString(3, documentReel.getISBN());
                    stmtReel.setInt(4, documentReel.getId());
                    
                    stmtReel.executeUpdate();
                }
                
                conn.commit();
                return true;
            }
            
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la mise à jour du document réel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                }
            }
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet DocumentReel.
     */
    private DocumentReel mapResultSetToDocumentReel(ResultSet rs) throws SQLException {
        DocumentReel documentReel = new DocumentReel();
        
        // Mapper les champs de Document directement depuis le ResultSet
        documentReel.setId(rs.getInt("id"));
        documentReel.setTitre(rs.getString("titre"));
        documentReel.setAuteur(rs.getString("auteur"));
        
        String genreStr = rs.getString("genre");
        if (genreStr != null) {
            documentReel.setGenre(com.infinitpages.util.constants.Genre.valueOf(genreStr));
        }
        
        documentReel.setFormat(rs.getString("format"));
        
        Date datePub = rs.getDate("date_publication");
        if (datePub != null) {
            documentReel.setDatePublication(datePub.toLocalDate());
        }
        
        documentReel.setResume(rs.getString("resume"));
        documentReel.setMotsCles(rs.getString("mots_cles"));
        documentReel.setPrixParJour(rs.getDouble("prix_par_jour"));
        documentReel.setDisponible(rs.getBoolean("disponible"));
        documentReel.setNombreConsultations(rs.getInt("nombre_consultations"));
        documentReel.setNombreEmprunts(rs.getInt("nombre_emprunts"));
        documentReel.setNoteGlobale(rs.getDouble("note_globale"));
        
        int idCategorie = rs.getInt("id_categorie");
        if (!rs.wasNull() && idCategorie > 0) {
            com.infinitpages.model.entity.Categorie categorie = new com.infinitpages.model.entity.Categorie();
            categorie.setId(idCategorie);
            documentReel.setCategorie(categorie);
        }
        
        // Mapper les champs spécifiques à DocumentReel
        documentReel.setEmplacement(rs.getString("emplacement"));
        documentReel.setCondition(rs.getString("condition_livre"));
        documentReel.setISBN(rs.getString("isbn"));
        
        return documentReel;
    }
}

