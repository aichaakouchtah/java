package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.CategorieDAO;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de CategorieDAO.
 */
public class CategorieDAOImpl implements CategorieDAO {
    
    @Override
    public Optional<Categorie> findById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategorie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de catégorie par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Categorie> findByNom(String nom) {
        String sql = "SELECT * FROM categorie WHERE nom = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategorie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de catégorie par nom: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Categorie> findAll() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les catégories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    @Override
    public List<Categorie> findRootCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie WHERE id_categorie_parente IS NULL ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories racines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    @Override
    public List<Categorie> findSubCategories(int idCategorieParente) {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie WHERE id_categorie_parente = ? ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategorieParente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategorie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des sous-catégories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    @Override
    public Categorie save(Categorie categorie) {
        String sql = "INSERT INTO categorie (nom, description, nombre_documents, id_categorie_parente) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getNombreDocuments());
            
            if (categorie.getCategorieParente() != null) {
                stmt.setInt(4, categorie.getCategorieParente().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categorie.setId(generatedKeys.getInt(1));
                        return categorie;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Categorie categorie) {
        String sql = "UPDATE categorie SET nom = ?, description = ?, nombre_documents = ?, " +
                     "id_categorie_parente = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getNombreDocuments());
            
            if (categorie.getCategorieParente() != null) {
                stmt.setInt(4, categorie.getCategorieParente().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, categorie.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean incrementerNombreDocuments(int idCategorie) {
        String sql = "UPDATE categorie SET nombre_documents = nombre_documents + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategorie);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation du nombre de documents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean decrementerNombreDocuments(int idCategorie) {
        String sql = "UPDATE categorie SET nombre_documents = GREATEST(0, nombre_documents - 1) WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategorie);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la décrémentation du nombre de documents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Categorie.
     */
    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        
        categorie.setId(rs.getInt("id"));
        categorie.setNom(rs.getString("nom"));
        categorie.setDescription(rs.getString("description"));
        categorie.setNombreDocuments(rs.getInt("nombre_documents"));
        
        int idCategorieParente = rs.getInt("id_categorie_parente");
        if (!rs.wasNull() && idCategorieParente > 0) {
            Categorie categorieParente = new Categorie();
            categorieParente.setId(idCategorieParente);
            categorie.setCategorieParente(categorieParente);
        }
        
        return categorie;
    }
}

