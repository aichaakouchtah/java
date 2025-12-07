package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Categorie;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Categorie.
 */
public interface CategorieDAO {
    
    /**
     * Trouve une catégorie par son ID.
     * 
     * @param id L'identifiant de la catégorie
     * @return Optional contenant la catégorie si trouvée
     */
    Optional<Categorie> findById(int id);
    
    /**
     * Trouve une catégorie par son nom.
     * 
     * @param nom Le nom de la catégorie
     * @return Optional contenant la catégorie si trouvée
     */
    Optional<Categorie> findByNom(String nom);
    
    /**
     * Trouve toutes les catégories.
     * 
     * @return Liste de toutes les catégories
     */
    List<Categorie> findAll();
    
    /**
     * Trouve toutes les catégories racines (sans parent).
     * 
     * @return Liste des catégories racines
     */
    List<Categorie> findRootCategories();
    
    /**
     * Trouve toutes les sous-catégories d'une catégorie parente.
     * 
     * @param idCategorieParente L'identifiant de la catégorie parente
     * @return Liste des sous-catégories
     */
    List<Categorie> findSubCategories(int idCategorieParente);
    
    /**
     * Sauvegarde une nouvelle catégorie.
     * 
     * @param categorie La catégorie à sauvegarder
     * @return La catégorie avec son ID généré
     */
    Categorie save(Categorie categorie);
    
    /**
     * Met à jour une catégorie existante.
     * 
     * @param categorie La catégorie à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Categorie categorie);
    
    /**
     * Supprime une catégorie par son ID.
     * 
     * @param id L'identifiant de la catégorie à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
    
    /**
     * Incrémente le compteur de documents dans la catégorie.
     * 
     * @param idCategorie L'identifiant de la catégorie
     * @return true si la mise à jour a réussi
     */
    boolean incrementerNombreDocuments(int idCategorie);
    
    /**
     * Décrémente le compteur de documents dans la catégorie.
     * 
     * @param idCategorie L'identifiant de la catégorie
     * @return true si la mise à jour a réussi
     */
    boolean decrementerNombreDocuments(int idCategorie);
}

