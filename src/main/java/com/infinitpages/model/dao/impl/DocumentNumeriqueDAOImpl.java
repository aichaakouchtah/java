package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.DocumentNumeriqueDAO;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.DocumentNumerique;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de DocumentNumeriqueDAO.
 */
public class DocumentNumeriqueDAOImpl extends DocumentDAOImpl implements DocumentNumeriqueDAO {
    
    // Implémentation de DocumentDAO.findById pour DocumentNumerique
    @Override
    public Optional<Document> findById(int id) {
        Optional<DocumentNumerique> documentNumerique = findDocumentNumeriqueById(id);
        return documentNumerique.map(dn -> (Document) dn);
    }
    
    // Méthode spécifique pour DocumentNumerique (utilisée en interne)
    private Optional<DocumentNumerique> findDocumentNumeriqueById(int id) {
        String sql = "SELECT d.*, dn.url, dn.chemin_fichier, dn.taille, dn.format_fichier, " +
                     "dn.hash_fichier, dn.telechargeable " +
                     "FROM document d " +
                     "INNER JOIN document_numerique dn ON d.id = dn.id_document " +
                     "WHERE d.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocumentNumerique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de document numérique par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Implémentation de DocumentDAO.findAll pour DocumentNumerique
    @Override
    public List<Document> findAll() {
        List<DocumentNumerique> documentsNumeriques = findAllDocumentNumeriques();
        List<Document> documents = new ArrayList<>();
        for (DocumentNumerique dn : documentsNumeriques) {
            documents.add((Document) dn);
        }
        return documents;
    }
    
    // Méthode spécifique pour DocumentNumerique (utilisée en interne)
    private List<DocumentNumerique> findAllDocumentNumeriques() {
        List<DocumentNumerique> documents = new ArrayList<>();
        String sql = "SELECT d.*, dn.url, dn.chemin_fichier, dn.taille, dn.format_fichier, " +
                     "dn.hash_fichier, dn.telechargeable " +
                     "FROM document d " +
                     "INNER JOIN document_numerique dn ON d.id = dn.id_document " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocumentNumerique(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les documents numériques: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<DocumentNumerique> findTelechargeables() {
        List<DocumentNumerique> documents = new ArrayList<>();
        String sql = "SELECT d.*, dn.url, dn.chemin_fichier, dn.taille, dn.format_fichier, " +
                     "dn.hash_fichier, dn.telechargeable " +
                     "FROM document d " +
                     "INNER JOIN document_numerique dn ON d.id = dn.id_document " +
                     "WHERE dn.telechargeable = TRUE " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocumentNumerique(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents téléchargeables: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<DocumentNumerique> findByFormatFichier(String formatFichier) {
        List<DocumentNumerique> documents = new ArrayList<>();
        String sql = "SELECT d.*, dn.url, dn.chemin_fichier, dn.taille, dn.format_fichier, " +
                     "dn.hash_fichier, dn.telechargeable " +
                     "FROM document d " +
                     "INNER JOIN document_numerique dn ON d.id = dn.id_document " +
                     "WHERE dn.format_fichier = ? " +
                     "ORDER BY d.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formatFichier);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocumentNumerique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par format: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public Optional<DocumentNumerique> findByHash(String hashFichier) {
        String sql = "SELECT d.*, dn.url, dn.chemin_fichier, dn.taille, dn.format_fichier, " +
                     "dn.hash_fichier, dn.telechargeable " +
                     "FROM document d " +
                     "INNER JOIN document_numerique dn ON d.id = dn.id_document " +
                     "WHERE dn.hash_fichier = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashFichier);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocumentNumerique(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par hash: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public DocumentNumerique save(DocumentNumerique documentNumerique) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. D'abord sauvegarder le document de base
            DocumentNumerique docBase = (DocumentNumerique) super.save(documentNumerique);
            
            if (docBase != null && docBase.getId() > 0) {
                // 2. Ensuite insérer dans document_numerique
                String sqlNumerique = "INSERT INTO document_numerique (id_document, url, chemin_fichier, " +
                                     "taille, format_fichier, hash_fichier, telechargeable) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmtNumerique = conn.prepareStatement(sqlNumerique)) {
                    stmtNumerique.setInt(1, docBase.getId());
                    stmtNumerique.setString(2, documentNumerique.getUrl());
                    stmtNumerique.setString(3, documentNumerique.getCheminFichier());
                    stmtNumerique.setDouble(4, documentNumerique.getTaille());
                    stmtNumerique.setString(5, documentNumerique.getFormatFichier());
                    stmtNumerique.setString(6, documentNumerique.getHashFichier());
                    stmtNumerique.setBoolean(7, documentNumerique.isTelechargeable());
                    
                    stmtNumerique.executeUpdate();
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
            System.err.println("Erreur lors de la sauvegarde du document numérique: " + e.getMessage());
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
    public boolean update(DocumentNumerique documentNumerique) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Mettre à jour le document de base
            boolean docUpdated = super.update(documentNumerique);
            
            if (docUpdated) {
                // 2. Mettre à jour document_numerique
                String sqlNumerique = "UPDATE document_numerique SET url = ?, chemin_fichier = ?, " +
                                     "taille = ?, format_fichier = ?, hash_fichier = ?, telechargeable = ? " +
                                     "WHERE id_document = ?";
                
                try (PreparedStatement stmtNumerique = conn.prepareStatement(sqlNumerique)) {
                    stmtNumerique.setString(1, documentNumerique.getUrl());
                    stmtNumerique.setString(2, documentNumerique.getCheminFichier());
                    stmtNumerique.setDouble(3, documentNumerique.getTaille());
                    stmtNumerique.setString(4, documentNumerique.getFormatFichier());
                    stmtNumerique.setString(5, documentNumerique.getHashFichier());
                    stmtNumerique.setBoolean(6, documentNumerique.isTelechargeable());
                    stmtNumerique.setInt(7, documentNumerique.getId());
                    
                    stmtNumerique.executeUpdate();
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
            System.err.println("Erreur lors de la mise à jour du document numérique: " + e.getMessage());
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
     * Mappe un ResultSet vers un objet DocumentNumerique.
     */
    private DocumentNumerique mapResultSetToDocumentNumerique(ResultSet rs) throws SQLException {
        DocumentNumerique documentNumerique = new DocumentNumerique();
        
        // Mapper les champs de Document (via la méthode parente)
        Document doc = mapResultSetToDocument(rs);
        documentNumerique.setId(doc.getId());
        documentNumerique.setTitre(doc.getTitre());
        documentNumerique.setAuteur(doc.getAuteur());
        documentNumerique.setGenre(doc.getGenre());
        documentNumerique.setFormat(doc.getFormat());
        documentNumerique.setDatePublication(doc.getDatePublication());
        documentNumerique.setResume(doc.getResume());
        documentNumerique.setMotsCles(doc.getMotsCles());
        documentNumerique.setPrixParJour(doc.getPrixParJour());
        documentNumerique.setDisponible(doc.isDisponible());
        documentNumerique.setNombreConsultations(doc.getNombreConsultations());
        documentNumerique.setNombreEmprunts(doc.getNombreEmprunts());
        documentNumerique.setNoteGlobale(doc.getNoteGlobale());
        documentNumerique.setCategorieEntity(doc.getCategorieEntity());
        
        // Mapper les champs spécifiques à DocumentNumerique
        documentNumerique.setUrl(rs.getString("url"));
        documentNumerique.setCheminFichier(rs.getString("chemin_fichier"));
        documentNumerique.setTaille(rs.getDouble("taille"));
        documentNumerique.setFormatFichier(rs.getString("format_fichier"));
        documentNumerique.setHashFichier(rs.getString("hash_fichier"));
        documentNumerique.setTelechargeable(rs.getBoolean("telechargeable"));
        
        return documentNumerique;
    }
}

