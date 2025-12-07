package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.constants.TypeUtilisateur;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de UtilisateurDAO.
 */
public class UtilisateurDAOImpl extends PersonneDAOImpl implements UtilisateurDAO {
    
    // Implémentation de PersonneDAO.findById pour Utilisateur
    @Override
    public Optional<Personne> findById(int id) {
        Optional<Utilisateur> utilisateur = findUtilisateurById(id);
        return utilisateur.map(u -> (Personne) u);
    }
    
    // Méthode spécifique pour Utilisateur (utilisée en interne)
    private Optional<Utilisateur> findUtilisateurById(int id) {
        String sql = "SELECT p.*, u.type_utilisateur, u.limite_emprunts, u.solde_a_payer " +
                     "FROM personne p " +
                     "INNER JOIN utilisateur u ON p.id = u.id_personne " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'utilisateur par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Implémentation de PersonneDAO.findByEmail pour Utilisateur
    @Override
    public Optional<Personne> findByEmail(String email) {
        Optional<Utilisateur> utilisateur = findUtilisateurByEmail(email);
        return utilisateur.map(u -> (Personne) u);
    }
    
    // Méthode spécifique pour Utilisateur (utilisée en interne)
    private Optional<Utilisateur> findUtilisateurByEmail(String email) {
        String sql = "SELECT p.*, u.type_utilisateur, u.limite_emprunts, u.solde_a_payer " +
                     "FROM personne p " +
                     "INNER JOIN utilisateur u ON p.id = u.id_personne " +
                     "WHERE p.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'utilisateur par email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Utilisateur> findByType(TypeUtilisateur typeUtilisateur) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT p.*, u.type_utilisateur, u.limite_emprunts, u.solde_a_payer " +
                     "FROM personne p " +
                     "INNER JOIN utilisateur u ON p.id = u.id_personne " +
                     "WHERE u.type_utilisateur = ? " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeUtilisateur.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'utilisateurs par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return utilisateurs;
    }
    
    @Override
    public List<Utilisateur> findAllActifs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT p.*, u.type_utilisateur, u.limite_emprunts, u.solde_a_payer " +
                     "FROM personne p " +
                     "INNER JOIN utilisateur u ON p.id = u.id_personne " +
                     "WHERE p.est_actif = TRUE " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs actifs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return utilisateurs;
    }
    
    @Override
    public List<Utilisateur> findWithSoldeAPayer() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT p.*, u.type_utilisateur, u.limite_emprunts, u.solde_a_payer " +
                     "FROM personne p " +
                     "INNER JOIN utilisateur u ON p.id = u.id_personne " +
                     "WHERE u.solde_a_payer > 0 " +
                     "ORDER BY u.solde_a_payer DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs avec solde: " + e.getMessage());
            e.printStackTrace();
        }
        
        return utilisateurs;
    }
    
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insérer dans personne
            String sqlPersonne = "INSERT INTO personne (nom, email, mot_de_passe, date_inscription, est_actif) " +
                                "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne, Statement.RETURN_GENERATED_KEYS)) {
                stmtPersonne.setString(1, utilisateur.getNom());
                stmtPersonne.setString(2, utilisateur.getEmail());
                stmtPersonne.setString(3, utilisateur.getMotDePasse());
                
                if (utilisateur.getDateInscription() != null) {
                    stmtPersonne.setTimestamp(4, Timestamp.valueOf(utilisateur.getDateInscription()));
                } else {
                    stmtPersonne.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
                }
                
                stmtPersonne.setBoolean(5, utilisateur.isEstActif());
                
                int affectedRows = stmtPersonne.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtPersonne.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idPersonne = generatedKeys.getInt(1);
                            utilisateur.setId(idPersonne);
                            
                            // 2. Insérer dans utilisateur
                            String sqlUtilisateur = "INSERT INTO utilisateur (id_personne, type_utilisateur, limite_emprunts, solde_a_payer) " +
                                                   "VALUES (?, ?, ?, ?)";
                            
                            try (PreparedStatement stmtUtilisateur = conn.prepareStatement(sqlUtilisateur)) {
                                stmtUtilisateur.setInt(1, idPersonne);
                                stmtUtilisateur.setString(2, utilisateur.getTypeUtilisateur().name());
                                stmtUtilisateur.setInt(3, utilisateur.getLimiteEmprunts());
                                stmtUtilisateur.setDouble(4, utilisateur.getSoldeAPayer());
                                
                                stmtUtilisateur.executeUpdate();
                            }
                            
                            conn.commit();
                            return utilisateur;
                        }
                    }
                }
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
            System.err.println("Erreur lors de la sauvegarde de l'utilisateur: " + e.getMessage());
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
    public boolean update(Utilisateur utilisateur) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Mettre à jour personne
            String sqlPersonne = "UPDATE personne SET nom = ?, email = ?, mot_de_passe = ?, est_actif = ? WHERE id = ?";
            
            try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne)) {
                stmtPersonne.setString(1, utilisateur.getNom());
                stmtPersonne.setString(2, utilisateur.getEmail());
                stmtPersonne.setString(3, utilisateur.getMotDePasse());
                stmtPersonne.setBoolean(4, utilisateur.isEstActif());
                stmtPersonne.setInt(5, utilisateur.getId());
                
                stmtPersonne.executeUpdate();
            }
            
            // 2. Mettre à jour utilisateur
            String sqlUtilisateur = "UPDATE utilisateur SET type_utilisateur = ?, limite_emprunts = ?, solde_a_payer = ? WHERE id_personne = ?";
            
            try (PreparedStatement stmtUtilisateur = conn.prepareStatement(sqlUtilisateur)) {
                stmtUtilisateur.setString(1, utilisateur.getTypeUtilisateur().name());
                stmtUtilisateur.setInt(2, utilisateur.getLimiteEmprunts());
                stmtUtilisateur.setDouble(3, utilisateur.getSoldeAPayer());
                stmtUtilisateur.setInt(4, utilisateur.getId());
                
                stmtUtilisateur.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
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
    
    @Override
    public boolean updateSoldeAPayer(int idUtilisateur, double nouveauSolde) {
        String sql = "UPDATE utilisateur SET solde_a_payer = ? WHERE id_personne = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, nouveauSolde);
            stmt.setInt(2, idUtilisateur);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du solde: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int countByType(TypeUtilisateur typeUtilisateur) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE type_utilisateur = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeUtilisateur.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers un objet Utilisateur.
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        
        // Mapper les champs de Personne
        mapPersonneFields(rs, utilisateur);
        
        // Mapper les champs spécifiques à Utilisateur
        String typeUtilisateurStr = rs.getString("type_utilisateur");
        if (typeUtilisateurStr != null) {
            utilisateur.setTypeUtilisateur(TypeUtilisateur.valueOf(typeUtilisateurStr));
        }
        
        utilisateur.setLimiteEmprunts(rs.getInt("limite_emprunts"));
        utilisateur.setSoldeAPayer(rs.getDouble("solde_a_payer"));
        
        return utilisateur;
    }
}

