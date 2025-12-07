package com.infinitpages.model.dao;

import com.infinitpages.model.entity.SuperAdmin;

/**
 * Interface DAO pour la classe SuperAdmin.
 * Étend AdminDAO avec des méthodes spécifiques aux super administrateurs.
 */
public interface SuperAdminDAO extends AdminDAO {
    
    // Note: findById(int), findByEmail(String) et findAll() sont héritées de AdminDAO/PersonneDAO
    // L'implémentation SuperAdminDAOImpl peut retourner les types spécifiques grâce au polymorphisme
    
    /**
     * Sauvegarde un nouveau super admin.
     * 
     * @param superAdmin Le super admin à sauvegarder
     * @return Le super admin avec son ID généré
     */
    SuperAdmin save(SuperAdmin superAdmin);
    
    /**
     * Vérifie si un admin est un super admin.
     * 
     * @param idAdmin L'identifiant de l'admin
     * @return true si c'est un super admin
     */
    boolean isSuperAdmin(int idAdmin);
}

