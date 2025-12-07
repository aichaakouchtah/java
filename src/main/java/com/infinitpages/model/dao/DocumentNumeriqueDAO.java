package com.infinitpages.model.dao;

import com.infinitpages.model.entity.DocumentNumerique;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe DocumentNumerique.
 * Étend DocumentDAO avec des méthodes spécifiques aux documents numériques.
 */
public interface DocumentNumeriqueDAO extends DocumentDAO {
    
    // Note: findById(int) et findAll() sont héritées de DocumentDAO
    // L'implémentation DocumentNumeriqueDAOImpl peut retourner Optional<DocumentNumerique> grâce au polymorphisme
    
    /**
     * Trouve tous les documents numériques téléchargeables.
     * 
     * @return Liste des documents téléchargeables
     */
    List<DocumentNumerique> findTelechargeables();
    
    /**
     * Trouve tous les documents numériques d'un format donné.
     * 
     * @param formatFichier Le format du fichier (PDF, EPUB, etc.)
     * @return Liste des documents du format spécifié
     */
    List<DocumentNumerique> findByFormatFichier(String formatFichier);
    
    /**
     * Trouve un document numérique par son hash.
     * 
     * @param hashFichier Le hash SHA-256 du fichier
     * @return Optional contenant le document numérique si trouvé
     */
    Optional<DocumentNumerique> findByHash(String hashFichier);
    
    /**
     * Sauvegarde un nouveau document numérique.
     * 
     * @param documentNumerique Le document numérique à sauvegarder
     * @return Le document numérique avec son ID généré
     */
    DocumentNumerique save(DocumentNumerique documentNumerique);
    
    /**
     * Met à jour un document numérique existant.
     * 
     * @param documentNumerique Le document numérique à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(DocumentNumerique documentNumerique);
}

