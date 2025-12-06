package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Emprunt;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Emprunt.
 */
public interface EmpruntDAO {
    
    /**
     * Trouve un emprunt par son ID.
     * 
     * @param id L'identifiant de l'emprunt
     * @return Optional contenant l'emprunt si trouvé
     */
    Optional<Emprunt> findById(int id);
    
    /**
     * Trouve tous les emprunts.
     * 
     * @return Liste de tous les emprunts
     */
    List<Emprunt> findAll();
    
    /**
     * Trouve tous les emprunts d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Liste des emprunts de l'utilisateur
     */
    List<Emprunt> findByUtilisateur(int idUtilisateur);
    
    /**
     * Trouve tous les emprunts d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @return Liste des emprunts du document
     */
    List<Emprunt> findByDocument(int idDocument);
    
    /**
     * Trouve tous les emprunts avec un état donné.
     * 
     * @param etat L'état (EN_COURS, RETOURNE, EN_RETARD)
     * @return Liste des emprunts avec cet état
     */
    List<Emprunt> findByEtat(String etat);
    
    /**
     * Trouve tous les emprunts actifs d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Liste des emprunts actifs
     */
    List<Emprunt> findActifsByUtilisateur(int idUtilisateur);
    
    /**
     * Trouve tous les emprunts en retard.
     * 
     * @return Liste des emprunts en retard
     */
    List<Emprunt> findEnRetard();
    
    /**
     * Trouve tous les emprunts en retard d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Liste des emprunts en retard
     */
    List<Emprunt> findEnRetardByUtilisateur(int idUtilisateur);
    
    /**
     * Compte le nombre d'emprunts actifs d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Le nombre d'emprunts actifs
     */
    int countEmpruntsActifs(int idUtilisateur);
    
    /**
     * Trouve tous les emprunts dans une période donnée.
     * 
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste des emprunts dans la période
     */
    List<Emprunt> findByPeriode(LocalDate dateDebut, LocalDate dateFin);
    
    /**
     * Sauvegarde un nouvel emprunt.
     * 
     * @param emprunt L'emprunt à sauvegarder
     * @return L'emprunt avec son ID généré
     */
    Emprunt save(Emprunt emprunt);
    
    /**
     * Met à jour un emprunt existant.
     * 
     * @param emprunt L'emprunt à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Emprunt emprunt);
    
    /**
     * Supprime un emprunt par son ID.
     * 
     * @param id L'identifiant de l'emprunt à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

