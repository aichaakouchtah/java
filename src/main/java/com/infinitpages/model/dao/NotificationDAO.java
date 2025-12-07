package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Notification;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Notification.
 */
public interface NotificationDAO {
    
    /**
     * Trouve une notification par son ID.
     * 
     * @param id L'identifiant de la notification
     * @return Optional contenant la notification si trouvée
     */
    Optional<Notification> findById(int id);
    
    /**
     * Trouve toutes les notifications d'un destinataire.
     * 
     * @param idDestinataire L'identifiant du destinataire
     * @return Liste des notifications du destinataire
     */
    List<Notification> findByDestinataire(int idDestinataire);
    
    /**
     * Trouve toutes les notifications non lues d'un destinataire.
     * 
     * @param idDestinataire L'identifiant du destinataire
     * @return Liste des notifications non lues
     */
    List<Notification> findNonLuesByDestinataire(int idDestinataire);
    
    /**
     * Trouve toutes les notifications d'un type donné.
     * 
     * @param type Le type (RAPPEL, ALERTE, INFO)
     * @return Liste des notifications du type
     */
    List<Notification> findByType(String type);
    
    /**
     * Trouve toutes les notifications non lues.
     * 
     * @return Liste des notifications non lues
     */
    List<Notification> findNonLues();
    
    /**
     * Compte le nombre de notifications non lues d'un destinataire.
     * 
     * @param idDestinataire L'identifiant du destinataire
     * @return Le nombre de notifications non lues
     */
    int countNonLues(int idDestinataire);
    
    /**
     * Marque une notification comme lue.
     * 
     * @param id L'identifiant de la notification
     * @return true si la mise à jour a réussi
     */
    boolean marquerCommeLue(int id);
    
    /**
     * Marque toutes les notifications d'un destinataire comme lues.
     * 
     * @param idDestinataire L'identifiant du destinataire
     * @return Le nombre de notifications mises à jour
     */
    int marquerToutesCommeLues(int idDestinataire);
    
    /**
     * Sauvegarde une nouvelle notification.
     * 
     * @param notification La notification à sauvegarder
     * @return La notification avec son ID généré
     */
    Notification save(Notification notification);
    
    /**
     * Met à jour une notification existante.
     * 
     * @param notification La notification à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Notification notification);
    
    /**
     * Supprime une notification par son ID.
     * 
     * @param id L'identifiant de la notification à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
    
    /**
     * Supprime toutes les notifications lues d'un destinataire.
     * 
     * @param idDestinataire L'identifiant du destinataire
     * @return Le nombre de notifications supprimées
     */
    int deleteLuesByDestinataire(int idDestinataire);
}

