package com.infinitpages.model.service;

import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;
import com.infinitpages.model.dao.impl.DocumentDAOImpl;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de gestion des emprunts.
 * Orchestre la logique métier pour les emprunts.
 * Les calculs sont délégués aux entités (Emprunt).
 */
public class LoanService {
    
    private EmpruntDAO empruntDAO;
    private DocumentDAO documentDAO;
    
    /**
     * Constructeur par défaut.
     */
    public LoanService() {
        this.empruntDAO = new EmpruntDAOImpl();
        this.documentDAO = new DocumentDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param empruntDAO Le DAO Emprunt à utiliser
     * @param documentDAO Le DAO Document à utiliser
     */
    public LoanService(EmpruntDAO empruntDAO, DocumentDAO documentDAO) {
        this.empruntDAO = empruntDAO;
        this.documentDAO = documentDAO;
    }
    
    /**
     * Emprunte un document pour un utilisateur.
     * 
     * @param utilisateur L'utilisateur qui emprunte
     * @param document Le document à emprunter
     * @return L'emprunt créé
     * @throws IllegalStateException Si l'utilisateur a atteint sa limite ou si le document n'est pas disponible
     */
    public Emprunt emprunterDocument(Utilisateur utilisateur, Document document) {
        // Validation métier
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        if (document == null) {
            throw new IllegalArgumentException("Document ne peut pas être null");
        }
        if (!document.isDisponible()) {
            throw new IllegalStateException("Le document n'est pas disponible");
        }
        
        // Vérifier si l'utilisateur a atteint sa limite d'emprunts
        int nombreEmpruntsActifs = empruntDAO.countEmpruntsActifs(utilisateur.getId());
        if (nombreEmpruntsActifs >= utilisateur.getLimiteEmprunts()) {
            throw new IllegalStateException("Limite d'emprunts atteinte");
        }
        
        // Créer l'emprunt
        int dureeMax = utilisateur.getDureeEmpruntJours();
        Emprunt emprunt = new Emprunt(utilisateur, document, LocalDate.now(), dureeMax);
        
        // Marquer le document comme non disponible
        document.setDisponible(false);
        
        // Sauvegarder en base de données
        try {
            emprunt = empruntDAO.save(emprunt);
            documentDAO.setDisponible(document.getId(), false);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'emprunt: " + e.getMessage(), e);
        }
        
        return emprunt;
    }
    
    /**
     * Retourne un document emprunté.
     * 
     * @param emprunt L'emprunt à retourner
     * @return L'emprunt mis à jour avec les calculs de pénalité
     */
    public Emprunt retournerDocument(Emprunt emprunt) {
        // Validation métier
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt ne peut pas être null");
        }
        if (emprunt.getDateRetourEffective() != null) {
            throw new IllegalStateException("Le document a déjà été retourné");
        }
        
        // Mettre à jour l'emprunt
        emprunt.setDateRetourEffective(LocalDate.now());
        emprunt.setEtat("RETOURNE");
        
        // Calculer la pénalité si retard (les calculs sont dans l'entité)
        if (emprunt.estEnRetard()) {
            double penalite = emprunt.calculerPenalite();
            emprunt.setPenalite(penalite);
        }
        
        // Rendre le document disponible
        Document document = emprunt.getDocument();
        if (document != null) {
            document.setDisponible(true);
        }
        
        // Sauvegarder en base de données
        try {
            empruntDAO.update(emprunt);
            if (document != null && document.getId() > 0) {
                documentDAO.setDisponible(document.getId(), true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du retour: " + e.getMessage(), e);
        }
        
        return emprunt;
    }
    
    /**
     * Calcule le montant total à payer pour un emprunt.
     * Délègue le calcul à l'entité Emprunt.
     * 
     * @param emprunt L'emprunt
     * @return Le montant total (prix + pénalité)
     */
    public double calculerMontantTotal(Emprunt emprunt) {
        if (emprunt == null) {
            return 0.0;
        }
        // Le calcul est fait par l'entité elle-même
        return emprunt.calculerMontantTotal();
    }
    
    /**
     * Récupère tous les emprunts actifs d'un utilisateur.
     * 
     * @param utilisateur L'utilisateur
     * @return Liste des emprunts actifs
     */
    public List<Emprunt> getEmpruntsActifs(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        
        try {
            return empruntDAO.findActifsByUtilisateur(utilisateur.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des emprunts: " + e.getMessage(), e);
        }
    }
}

