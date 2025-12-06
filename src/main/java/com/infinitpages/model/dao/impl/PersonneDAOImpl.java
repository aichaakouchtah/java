package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.PersonneDAO;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de PersonneDAO.
 */
public class PersonneDAOImpl implements PersonneDAO {
    
    @Override
    public Optional<Personne> findById(int id) {
        String sql = "SELECT * FROM personne WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de personne par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Personne> findByEmail(String email) {
        String sql = "SELECT * FROM personne WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de personne par email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Personne> findAll() {
        List<Personne> personnes = new ArrayList<>();
        String sql = "SELECT * FROM personne ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les personnes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return personnes;
    }
    
    @Override
    public List<Personne> findAllActives() {
        List<Personne> personnes = new ArrayList<>();
        String sql = "SELECT * FROM personne WHERE est_actif = TRUE ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des personnes actives: " + e.getMessage());
            e.printStackTrace();
        }
        
        return personnes;
    }
    
    @Override
    public Personne save(Personne personne) {
        String sql = "INSERT INTO personne (nom, email, mot_de_passe, date_inscription, est_actif) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getEmail());
            stmt.setString(3, personne.getMotDePasse());
            
            if (personne.getDateInscription() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(personne.getDateInscription()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            stmt.setBoolean(5, personne.isEstActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        personne.setId(generatedKeys.getInt(1));
                        return personne;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de la personne: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Personne personne) {
        String sql = "UPDATE personne SET nom = ?, email = ?, mot_de_passe = ?, est_actif = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getEmail());
            stmt.setString(3, personne.getMotDePasse());
            stmt.setBoolean(4, personne.isEstActif());
            stmt.setInt(5, personne.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la personne: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM personne WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la personne: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM personne WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public Optional<Personne> authenticate(String email, String motDePasse) {
        // Note: En production, le mot de passe devrait être hashé (BCrypt, etc.)
        // Ici, on fait une comparaison simple pour l'exemple
        String sql = "SELECT * FROM personne WHERE email = ? AND mot_de_passe = ? AND est_actif = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Mappe un ResultSet vers un objet Personne.
     * Note: Cette méthode est protégée et doit être surchargée par les sous-classes
     * car Personne est abstraite. Cette implémentation de base retourne null
     * et doit être utilisée uniquement pour les opérations communes.
     */
    protected Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        // Cette méthode ne devrait pas être appelée directement
        // Les sous-classes doivent la surcharger pour créer les bonnes instances
        throw new UnsupportedOperationException(
            "mapResultSetToPersonne doit être surchargée par les sous-classes"
        );
    }
    
    /**
     * Méthode utilitaire pour mapper les champs communs de Personne.
     * À utiliser par les sous-classes.
     */
    protected void mapPersonneFields(ResultSet rs, Personne personne) throws SQLException {
        personne.setId(rs.getInt("id"));
        personne.setNom(rs.getString("nom"));
        personne.setEmail(rs.getString("email"));
        personne.setMotDePasse(rs.getString("mot_de_passe"));
        
        Timestamp timestamp = rs.getTimestamp("date_inscription");
        if (timestamp != null) {
            personne.setDateInscription(timestamp.toLocalDateTime());
        }
        
        personne.setEstActif(rs.getBoolean("est_actif"));
    }
}

