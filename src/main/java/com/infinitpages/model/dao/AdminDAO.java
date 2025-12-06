package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Admin;
import com.infinitpages.util.constants.TypeAdmin;
import java.util.List;

/**
 * Interface DAO pour la classe Admin.
 * Étend PersonneDAO avec des méthodes spécifiques aux administrateurs.
 */
public interface AdminDAO extends PersonneDAO {
    
    // Note: findById(int) et findByEmail(String) sont héritées de PersonneDAO
    // L'implémentation AdminDAOImpl peut retourner Optional<Admin> grâce au polymorphisme
    
    /**
     * Trouve tous les admins d'un type donné.
     * 
     * @param typeAdmin Le type d'admin
     * @return Liste des admins du type spécifié
     */
    List<Admin> findByType(TypeAdmin typeAdmin);
    
    /**
     * Trouve tous les admins d'un département.
     * 
     * @param departement Le département
     * @return Liste des admins du département
     */
    List<Admin> findByDepartement(String departement);
    
    /**
     * Trouve tous les admins actifs.
     * 
     * @return Liste des admins actifs
     */
    List<Admin> findAllActifs();
    
    /**
     * Sauvegarde un nouvel admin.
     * 
     * @param admin L'admin à sauvegarder
     * @return L'admin avec son ID généré
     */
    Admin save(Admin admin);
    
    /**
     * Met à jour un admin existant.
     * 
     * @param admin L'admin à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Admin admin);
    
    /**
     * Compte le nombre d'admins par type.
     * 
     * @param typeAdmin Le type d'admin
     * @return Le nombre d'admins
     */
    int countByType(TypeAdmin typeAdmin);
}

