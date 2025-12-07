package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.AdminDAO;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.util.constants.TypeAdmin;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de AdminDAO.
 */
public class AdminDAOImpl extends PersonneDAOImpl implements AdminDAO {
    
    // Implémentation de PersonneDAO.findById pour Admin
    @Override
    public Optional<Personne> findById(int id) {
        Optional<Admin> admin = findAdminById(id);
        return admin.map(a -> (Personne) a);
    }
    
    // Méthode spécifique pour Admin (utilisée en interne)
    private Optional<Admin> findAdminById(int id) {
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'admin par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Implémentation de PersonneDAO.findByEmail pour Admin
    @Override
    public Optional<Personne> findByEmail(String email) {
        Optional<Admin> admin = findAdminByEmail(email);
        return admin.map(a -> (Personne) a);
    }
    
    // Méthode spécifique pour Admin (utilisée en interne)
    private Optional<Admin> findAdminByEmail(String email) {
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "WHERE p.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'admin par email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Admin> findByType(TypeAdmin typeAdmin) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "WHERE a.type_admin = ? " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeAdmin.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'admins par type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return admins;
    }
    
    @Override
    public List<Admin> findByDepartement(String departement) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "WHERE a.departement = ? " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, departement);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'admins par département: " + e.getMessage());
            e.printStackTrace();
        }
        
        return admins;
    }
    
    @Override
    public List<Admin> findAllActifs() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "WHERE p.est_actif = TRUE " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des admins actifs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return admins;
    }
    
    @Override
    public Admin save(Admin admin) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insérer dans personne
            String sqlPersonne = "INSERT INTO personne (nom, email, mot_de_passe, date_inscription, est_actif) " +
                                "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne, Statement.RETURN_GENERATED_KEYS)) {
                stmtPersonne.setString(1, admin.getNom());
                stmtPersonne.setString(2, admin.getEmail());
                stmtPersonne.setString(3, admin.getMotDePasse());
                
                if (admin.getDateInscription() != null) {
                    stmtPersonne.setTimestamp(4, Timestamp.valueOf(admin.getDateInscription()));
                } else {
                    stmtPersonne.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
                }
                
                stmtPersonne.setBoolean(5, admin.isEstActif());
                
                int affectedRows = stmtPersonne.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtPersonne.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idPersonne = generatedKeys.getInt(1);
                            admin.setId(idPersonne);
                            
                            // 2. Insérer dans admin
                            String sqlAdmin = "INSERT INTO admin (id_personne, type_admin, departement) " +
                                             "VALUES (?, ?, ?)";
                            
                            try (PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin)) {
                                stmtAdmin.setInt(1, idPersonne);
                                stmtAdmin.setString(2, admin.getTypeAdmin().name());
                                stmtAdmin.setString(3, admin.getDepartement());
                                
                                stmtAdmin.executeUpdate();
                            }
                            
                            conn.commit();
                            return admin;
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
            System.err.println("Erreur lors de la sauvegarde de l'admin: " + e.getMessage());
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
    public boolean update(Admin admin) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Mettre à jour personne
            String sqlPersonne = "UPDATE personne SET nom = ?, email = ?, mot_de_passe = ?, est_actif = ? WHERE id = ?";
            
            try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne)) {
                stmtPersonne.setString(1, admin.getNom());
                stmtPersonne.setString(2, admin.getEmail());
                stmtPersonne.setString(3, admin.getMotDePasse());
                stmtPersonne.setBoolean(4, admin.isEstActif());
                stmtPersonne.setInt(5, admin.getId());
                
                stmtPersonne.executeUpdate();
            }
            
            // 2. Mettre à jour admin
            String sqlAdmin = "UPDATE admin SET type_admin = ?, departement = ? WHERE id_personne = ?";
            
            try (PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin)) {
                stmtAdmin.setString(1, admin.getTypeAdmin().name());
                stmtAdmin.setString(2, admin.getDepartement());
                stmtAdmin.setInt(3, admin.getId());
                
                stmtAdmin.executeUpdate();
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
            System.err.println("Erreur lors de la mise à jour de l'admin: " + e.getMessage());
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
    public int countByType(TypeAdmin typeAdmin) {
        String sql = "SELECT COUNT(*) FROM admin WHERE type_admin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeAdmin.name());
            
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
     * Mappe un ResultSet vers un objet Admin.
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        
        // Mapper les champs de Personne
        mapPersonneFields(rs, admin);
        
        // Mapper les champs spécifiques à Admin
        String typeAdminStr = rs.getString("type_admin");
        if (typeAdminStr != null) {
            admin.setTypeAdmin(TypeAdmin.valueOf(typeAdminStr));
        }
        
        admin.setDepartement(rs.getString("departement"));
        
        return admin;
    }
}

