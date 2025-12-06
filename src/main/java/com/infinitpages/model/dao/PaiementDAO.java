package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Paiement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Paiement.
 */
public interface PaiementDAO {
    
    /**
     * Trouve un paiement par son ID.
     * 
     * @param id L'identifiant du paiement
     * @return Optional contenant le paiement si trouvé
     */
    Optional<Paiement> findById(int id);
    
    /**
     * Trouve tous les paiements.
     * 
     * @return Liste de tous les paiements
     */
    List<Paiement> findAll();
    
    /**
     * Trouve tous les paiements d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Liste des paiements de l'utilisateur
     */
    List<Paiement> findByUtilisateur(int idUtilisateur);
    
    /**
     * Trouve tous les paiements liés à un emprunt.
     * 
     * @param idEmprunt L'identifiant de l'emprunt
     * @return Liste des paiements de l'emprunt
     */
    List<Paiement> findByEmprunt(int idEmprunt);
    
    /**
     * Trouve tous les paiements avec un statut donné.
     * 
     * @param statut Le statut (EN_ATTENTE, VALIDE, ANNULE, REFUSE)
     * @return Liste des paiements avec ce statut
     */
    List<Paiement> findByStatut(String statut);
    
    /**
     * Trouve tous les paiements dans une période donnée.
     * 
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste des paiements dans la période
     */
    List<Paiement> findByPeriode(LocalDate dateDebut, LocalDate dateFin);
    
    /**
     * Trouve un paiement par sa référence.
     * 
     * @param reference La référence du paiement
     * @return Optional contenant le paiement si trouvé
     */
    Optional<Paiement> findByReference(String reference);
    
    /**
     * Calcule le total des paiements d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Le montant total payé
     */
    double getTotalPaiements(int idUtilisateur);
    
    /**
     * Sauvegarde un nouveau paiement.
     * 
     * @param paiement Le paiement à sauvegarder
     * @return Le paiement avec son ID généré
     */
    Paiement save(Paiement paiement);
    
    /**
     * Met à jour un paiement existant.
     * 
     * @param paiement Le paiement à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Paiement paiement);
    
    /**
     * Supprime un paiement par son ID.
     * 
     * @param id L'identifiant du paiement à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

