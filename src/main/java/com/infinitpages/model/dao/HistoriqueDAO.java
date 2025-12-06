package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Historique;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Historique.
 */
public interface HistoriqueDAO {
    
    /**
     * Trouve un historique par son ID.
     * 
     * @param id L'identifiant de l'historique
     * @return Optional contenant l'historique si trouvé
     */
    Optional<Historique> findById(int id);
    
    /**
     * Trouve tous les historiques d'une personne.
     * 
     * @param idPersonne L'identifiant de la personne
     * @return Liste des historiques de la personne
     */
    List<Historique> findByPersonne(int idPersonne);
    
    /**
     * Trouve tous les historiques d'un type donné.
     * 
     * @param type Le type (HISTORIQUE ou LISTE_LECTURE)
     * @return Liste des historiques du type
     */
    List<Historique> findByType(String type);
    
    /**
     * Trouve tous les historiques d'une personne et d'un type.
     * 
     * @param idPersonne L'identifiant de la personne
     * @param type Le type
     * @return Liste des historiques correspondants
     */
    List<Historique> findByPersonneAndType(int idPersonne, String type);
    
    /**
     * Trouve toutes les listes de lecture partagées.
     * 
     * @return Liste des listes de lecture partagées
     */
    List<Historique> findListesPartagees();
    
    /**
     * Trouve tous les documents d'un historique.
     * 
     * @param idHistorique L'identifiant de l'historique
     * @return Liste des documents de l'historique
     */
    List<Document> findDocumentsByHistorique(int idHistorique);
    
    /**
     * Ajoute un document à un historique.
     * 
     * @param idHistorique L'identifiant de l'historique
     * @param idDocument L'identifiant du document
     * @return true si l'ajout a réussi
     */
    boolean addDocument(int idHistorique, int idDocument);
    
    /**
     * Retire un document d'un historique.
     * 
     * @param idHistorique L'identifiant de l'historique
     * @param idDocument L'identifiant du document
     * @return true si le retrait a réussi
     */
    boolean removeDocument(int idHistorique, int idDocument);
    
    /**
     * Sauvegarde un nouvel historique.
     * 
     * @param historique L'historique à sauvegarder
     * @return L'historique avec son ID généré
     */
    Historique save(Historique historique);
    
    /**
     * Met à jour un historique existant.
     * 
     * @param historique L'historique à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Historique historique);
    
    /**
     * Supprime un historique par son ID.
     * 
     * @param id L'identifiant de l'historique à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

