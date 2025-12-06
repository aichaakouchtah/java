package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Avis;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Avis.
 */
public interface AvisDAO {
    
    /**
     * Trouve un avis par son ID.
     * 
     * @param id L'identifiant de l'avis
     * @return Optional contenant l'avis si trouvé
     */
    Optional<Avis> findById(int id);
    
    /**
     * Trouve tous les avis.
     * 
     * @return Liste de tous les avis
     */
    List<Avis> findAll();
    
    /**
     * Trouve tous les avis d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @return Liste des avis du document
     */
    List<Avis> findByDocument(int idDocument);
    
    /**
     * Trouve tous les avis modérés d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @return Liste des avis modérés
     */
    List<Avis> findModeresByDocument(int idDocument);
    
    /**
     * Trouve tous les avis non modérés.
     * 
     * @return Liste des avis non modérés
     */
    List<Avis> findNonModeres();
    
    /**
     * Trouve tous les avis d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Liste des avis de l'utilisateur
     */
    List<Avis> findByUtilisateur(int idUtilisateur);
    
    /**
     * Trouve l'avis d'un utilisateur sur un document.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @param idDocument L'identifiant du document
     * @return Optional contenant l'avis si trouvé
     */
    Optional<Avis> findByUtilisateurAndDocument(int idUtilisateur, int idDocument);
    
    /**
     * Calcule la note moyenne d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @return La note moyenne (0.0 si aucun avis)
     */
    double calculerNoteGlobale(int idDocument);
    
    /**
     * Compte le nombre d'avis d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @return Le nombre d'avis
     */
    int countByDocument(int idDocument);
    
    /**
     * Sauvegarde un nouvel avis.
     * 
     * @param avis L'avis à sauvegarder
     * @return L'avis avec son ID généré
     */
    Avis save(Avis avis);
    
    /**
     * Met à jour un avis existant.
     * 
     * @param avis L'avis à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Avis avis);
    
    /**
     * Supprime un avis par son ID.
     * 
     * @param id L'identifiant de l'avis à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

