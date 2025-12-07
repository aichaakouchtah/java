package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.model.entity.Document;
import com.infinitpages.util.constants.Genre;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de DocumentDAO.
 */
public class DocumentDAOImpl implements DocumentDAO {
    
    @Override
    public Optional<Document> findById(int id) {
        String sql = "SELECT * FROM document WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de document par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Document> findAll() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les documents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findDisponibles() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE disponible = TRUE ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findByTitre(String titre) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE titre LIKE ? ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par titre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findByAuteur(String auteur) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE auteur LIKE ? ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + auteur + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par auteur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findByGenre(Genre genre) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE genre = ? ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, genre.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par genre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findByCategorie(int idCategorie) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE id_categorie = ? ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategorie);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> search(String recherche) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document " +
                     "WHERE MATCH(titre, auteur, resume, mots_cles) AGAINST(? IN NATURAL LANGUAGE MODE) " +
                     "OR titre LIKE ? OR auteur LIKE ? OR resume LIKE ? OR mots_cles LIKE ? " +
                     "ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + recherche + "%";
            stmt.setString(1, recherche);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findMostConsulted(int limit) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document ORDER BY nombre_consultations DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des plus consultés: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findMostBorrowed(int limit) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document ORDER BY nombre_emprunts DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des plus empruntés: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public List<Document> findBestRated(int limit) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE note_globale > 0 ORDER BY note_globale DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des mieux notés: " + e.getMessage());
            e.printStackTrace();
        }
        
        return documents;
    }
    
    @Override
    public Document save(Document document) {
        String sql = "INSERT INTO document (titre, auteur, genre, format, date_publication, resume, " +
                     "mots_cles, prix_par_jour, disponible, nombre_consultations, nombre_emprunts, " +
                     "note_globale, id_categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, document.getTitre());
            stmt.setString(2, document.getAuteur());
            stmt.setString(3, document.getGenre() != null ? document.getGenre().name() : null);
            stmt.setString(4, document.getFormat());
            
            if (document.getDatePublication() != null) {
                stmt.setDate(5, Date.valueOf(document.getDatePublication()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, document.getResume());
            // Convertir List<String> en String (séparé par virgules)
            String motsClesStr = document.getMotsCles() != null && !document.getMotsCles().isEmpty()
                    ? String.join(",", document.getMotsCles()) : null;
            stmt.setString(7, motsClesStr);
            stmt.setDouble(8, document.getPrixParJour());
            stmt.setBoolean(9, document.isDisponible());
            stmt.setInt(10, document.getNombreConsultations());
            stmt.setInt(11, document.getNombreEmprunts());
            stmt.setDouble(12, document.getNoteGlobale());
            
            // Utiliser getCategorieEntity() pour obtenir l'objet Categorie
            if (document.getCategorieEntity() != null) {
                stmt.setInt(13, document.getCategorieEntity().getId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        document.setId(generatedKeys.getInt(1));
                        return document;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Document document) {
        String sql = "UPDATE document SET titre = ?, auteur = ?, genre = ?, format = ?, " +
                     "date_publication = ?, resume = ?, mots_cles = ?, prix_par_jour = ?, " +
                     "disponible = ?, nombre_consultations = ?, nombre_emprunts = ?, " +
                     "note_globale = ?, id_categorie = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, document.getTitre());
            stmt.setString(2, document.getAuteur());
            stmt.setString(3, document.getGenre() != null ? document.getGenre().name() : null);
            stmt.setString(4, document.getFormat());
            
            if (document.getDatePublication() != null) {
                stmt.setDate(5, Date.valueOf(document.getDatePublication()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, document.getResume());
            // Convertir List<String> en String (séparé par virgules)
            String motsClesStr = document.getMotsCles() != null && !document.getMotsCles().isEmpty()
                    ? String.join(",", document.getMotsCles()) : null;
            stmt.setString(7, motsClesStr);
            stmt.setDouble(8, document.getPrixParJour());
            stmt.setBoolean(9, document.isDisponible());
            stmt.setInt(10, document.getNombreConsultations());
            stmt.setInt(11, document.getNombreEmprunts());
            stmt.setDouble(12, document.getNoteGlobale());
            
            // Utiliser getCategorieEntity() pour obtenir l'objet Categorie
            if (document.getCategorieEntity() != null) {
                stmt.setInt(13, document.getCategorieEntity().getId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }
            
            stmt.setInt(14, document.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM document WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean incrementerConsultations(int idDocument) {
        String sql = "UPDATE document SET nombre_consultations = nombre_consultations + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean incrementerEmprunts(int idDocument) {
        String sql = "UPDATE document SET nombre_emprunts = nombre_emprunts + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation des emprunts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean updateNoteGlobale(int idDocument, double noteGlobale) {
        String sql = "UPDATE document SET note_globale = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, noteGlobale);
            stmt.setInt(2, idDocument);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la note globale: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean setDisponible(int idDocument, boolean disponible) {
        String sql = "UPDATE document SET disponible = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, disponible);
            stmt.setInt(2, idDocument);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la disponibilité: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM document";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des documents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int countDisponibles() {
        String sql = "SELECT COUNT(*) FROM document WHERE disponible = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des documents disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers un objet Document.
     * Note: Cette méthode crée une instance de Document abstraite.
     * Les sous-classes doivent surcharger cette méthode.
     */
    protected Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        // Créer une instance concrète - dans les sous-classes, on utilisera DocumentReel ou DocumentNumerique
        Document document = new Document() {
            // Classe anonyme pour instancier Document abstraite
        };
        
        document.setId(rs.getInt("id"));
        document.setTitre(rs.getString("titre"));
        document.setAuteur(rs.getString("auteur"));
        
        String genreStr = rs.getString("genre");
        if (genreStr != null) {
            document.setGenre(Genre.valueOf(genreStr));
        }
        
        document.setFormat(rs.getString("format"));
        
        Date datePub = rs.getDate("date_publication");
        if (datePub != null) {
            document.setDatePublication(datePub.toLocalDate());
        }
        
        document.setResume(rs.getString("resume"));
        // Convertir String en List<String>
        String motsClesStr = rs.getString("mots_cles");
        if (motsClesStr != null && !motsClesStr.isEmpty()) {
            List<String> motsCles = new ArrayList<>(Arrays.asList(motsClesStr.split(",")));
            document.setMotsCles(motsCles);
        } else {
            document.setMotsCles(new ArrayList<>());
        }
        document.setPrixParJour(rs.getDouble("prix_par_jour"));
        document.setDisponible(rs.getBoolean("disponible"));
        document.setNombreConsultations(rs.getInt("nombre_consultations"));
        document.setNombreEmprunts(rs.getInt("nombre_emprunts"));
        document.setNoteGlobale(rs.getDouble("note_globale"));
        
        int idCategorie = rs.getInt("id_categorie");
        if (!rs.wasNull() && idCategorie > 0) {
            Categorie categorie = new Categorie();
            categorie.setId(idCategorie);
            document.setCategorieEntity(categorie);
        }
        
        return document;
    }
}

