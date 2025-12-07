package com.infinitpages.model.dao;

import com.infinitpages.model.entity.DocumentReel;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe DocumentReel.
 * Étend DocumentDAO avec des méthodes spécifiques aux documents réels.
 */
public interface DocumentReelDAO extends DocumentDAO {
    
    // Note: findById(int) et findAll() sont héritées de DocumentDAO
    // L'implémentation DocumentReelDAOImpl peut retourner Optional<DocumentReel> grâce au polymorphisme
    
    /**
     * Trouve un document réel par son ISBN.
     * 
     * @param isbn L'ISBN du document
     * @return Optional contenant le document réel si trouvé
     */
    Optional<DocumentReel> findByISBN(String isbn);
    
    /**
     * Trouve tous les documents réels à un emplacement donné.
     * 
     * @param emplacement L'emplacement
     * @return Liste des documents à cet emplacement
     */
    List<DocumentReel> findByEmplacement(String emplacement);
    
    /**
     * Trouve tous les documents réels avec une condition donnée.
     * 
     * @param condition La condition du livre
     * @return Liste des documents avec cette condition
     */
    List<DocumentReel> findByCondition(String condition);
    
    /**
     * Sauvegarde un nouveau document réel.
     * 
     * @param documentReel Le document réel à sauvegarder
     * @return Le document réel avec son ID généré
     */
    DocumentReel save(DocumentReel documentReel);
    
    /**
     * Met à jour un document réel existant.
     * 
     * @param documentReel Le document réel à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(DocumentReel documentReel);
}

