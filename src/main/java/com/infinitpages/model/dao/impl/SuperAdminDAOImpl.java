package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.SuperAdminDAO;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.SuperAdmin;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de SuperAdminDAO.
 */
public class SuperAdminDAOImpl extends AdminDAOImpl implements SuperAdminDAO {
    
    // Implémentation de PersonneDAO.findById pour SuperAdmin
    @Override
    public Optional<Personne> findById(int id) {
        Optional<SuperAdmin> superAdmin = findSuperAdminById(id);
        return superAdmin.map(sa -> (Personne) sa);
    }
    
    // Méthode spécifique pour SuperAdmin (utilisée en interne)
    private Optional<SuperAdmin> findSuperAdminById(int id) {
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "INNER JOIN superadmin sa ON a.id_personne = sa.id_admin " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSuperAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de super admin par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Implémentation de PersonneDAO.findAll pour SuperAdmin
    @Override
    public List<Personne> findAll() {
        List<SuperAdmin> superAdmins = findAllSuperAdmins();
        List<Personne> personnes = new ArrayList<>();
        for (SuperAdmin sa : superAdmins) {
            personnes.add((Personne) sa);
        }
        return personnes;
    }
    
    // Méthode spécifique pour SuperAdmin (utilisée en interne)
    private List<SuperAdmin> findAllSuperAdmins() {
        List<SuperAdmin> superAdmins = new ArrayList<>();
        String sql = "SELECT p.*, a.type_admin, a.departement " +
                     "FROM personne p " +
                     "INNER JOIN admin a ON p.id = a.id_personne " +
                     "INNER JOIN superadmin sa ON a.id_personne = sa.id_admin " +
                     "ORDER BY p.nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                superAdmins.add(mapResultSetToSuperAdmin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les super admins: " + e.getMessage());
            e.printStackTrace();
        }
        
        return superAdmins;
    }
    
    @Override
    public SuperAdmin save(SuperAdmin superAdmin) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. D'abord sauvegarder comme Admin (via la méthode parente)
            AdminDAOImpl adminDAO = new AdminDAOImpl();
            com.infinitpages.model.entity.Admin admin = adminDAO.save(superAdmin);
            
            if (admin != null && admin.getId() > 0) {
                // 2. Ensuite insérer dans superadmin
                String sqlSuperAdmin = "INSERT INTO superadmin (id_admin) VALUES (?)";
                
                try (PreparedStatement stmtSuperAdmin = conn.prepareStatement(sqlSuperAdmin)) {
                    stmtSuperAdmin.setInt(1, admin.getId());
                    stmtSuperAdmin.executeUpdate();
                }
                
                conn.commit();
                superAdmin.setId(admin.getId());
                return superAdmin;
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
            System.err.println("Erreur lors de la sauvegarde du super admin: " + e.getMessage());
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
    public boolean isSuperAdmin(int idAdmin) {
        String sql = "SELECT COUNT(*) FROM superadmin WHERE id_admin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAdmin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du super admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet SuperAdmin.
     */
    private SuperAdmin mapResultSetToSuperAdmin(ResultSet rs) throws SQLException {
        SuperAdmin superAdmin = new SuperAdmin();
        
        // Mapper les champs de Personne et Admin
        mapPersonneFields(rs, superAdmin);
        
        String typeAdminStr = rs.getString("type_admin");
        if (typeAdminStr != null) {
            superAdmin.setTypeAdmin(com.infinitpages.util.constants.TypeAdmin.valueOf(typeAdminStr));
        }
        
        superAdmin.setDepartement(rs.getString("departement"));
        
        return superAdmin;
    }
}

