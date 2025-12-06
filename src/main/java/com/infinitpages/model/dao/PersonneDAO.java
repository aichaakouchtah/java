package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Personne;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Personne.
 * Méthodes de base pour toutes les personnes (Utilisateur, Admin, SuperAdmin).
 */
public interface PersonneDAO {
    
    /**
     * Trouve une personne par son ID.
     * 
     * @param id L'identifiant de la personne
     * @return Optional contenant la personne si trouvée
     */
    Optional<Personne> findById(int id);
    
    /**
     * Trouve une personne par son email.
     * 
     * @param email L'email de la personne
     * @return Optional contenant la personne si trouvée
     */
    Optional<Personne> findByEmail(String email);
    
    /**
     * Trouve toutes les personnes.
     * 
     * @return Liste de toutes les personnes
     */
    List<Personne> findAll();
    
    /**
     * Trouve toutes les personnes actives.
     * 
     * @return Liste des personnes actives
     */
    List<Personne> findAllActives();
    
    /**
     * Sauvegarde une nouvelle personne.
     * 
     * @param personne La personne à sauvegarder
     * @return La personne avec son ID généré
     */
    Personne save(Personne personne);
    
    /**
     * Met à jour une personne existante.
     * 
     * @param personne La personne à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Personne personne);
    
    /**
     * Supprime une personne par son ID.
     * 
     * @param id L'identifiant de la personne à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
    
    /**
     * Vérifie si une personne existe avec cet email.
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifie les identifiants de connexion.
     * 
     * @param email L'email
     * @param motDePasse Le mot de passe (en clair, à hasher avant comparaison)
     * @return Optional contenant la personne si les identifiants sont corrects
     */
    Optional<Personne> authenticate(String email, String motDePasse);
}

