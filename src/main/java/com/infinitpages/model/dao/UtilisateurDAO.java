package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.constants.TypeUtilisateur;
import java.util.List;

/**
 * Interface DAO pour la classe Utilisateur.
 * Étend PersonneDAO avec des méthodes spécifiques aux utilisateurs.
 */
public interface UtilisateurDAO extends PersonneDAO {
    
    // Note: findById(int) et findByEmail(String) sont héritées de PersonneDAO
    // L'implémentation UtilisateurDAOImpl peut retourner Optional<Utilisateur> grâce au polymorphisme
    
    /**
     * Trouve tous les utilisateurs d'un type donné.
     * 
     * @param typeUtilisateur Le type d'utilisateur
     * @return Liste des utilisateurs du type spécifié
     */
    List<Utilisateur> findByType(TypeUtilisateur typeUtilisateur);
    
    /**
     * Trouve tous les utilisateurs actifs.
     * 
     * @return Liste des utilisateurs actifs
     */
    List<Utilisateur> findAllActifs();
    
    /**
     * Trouve tous les utilisateurs avec un solde à payer > 0.
     * 
     * @return Liste des utilisateurs avec des pénalités
     */
    List<Utilisateur> findWithSoldeAPayer();
    
    /**
     * Sauvegarde un nouvel utilisateur.
     * 
     * @param utilisateur L'utilisateur à sauvegarder
     * @return L'utilisateur avec son ID généré
     */
    Utilisateur save(Utilisateur utilisateur);
    
    /**
     * Met à jour un utilisateur existant.
     * 
     * @param utilisateur L'utilisateur à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Utilisateur utilisateur);
    
    /**
     * Met à jour le solde à payer d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @param nouveauSolde Le nouveau solde
     * @return true si la mise à jour a réussi
     */
    boolean updateSoldeAPayer(int idUtilisateur, double nouveauSolde);
    
    /**
     * Compte le nombre d'utilisateurs par type.
     * 
     * @param typeUtilisateur Le type d'utilisateur
     * @return Le nombre d'utilisateurs
     */
    int countByType(TypeUtilisateur typeUtilisateur);
}

