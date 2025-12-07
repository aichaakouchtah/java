package com.infinitpages.controller;

import com.infinitpages.model.entity.Paiement;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.service.PaymentService;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des paiements.
 * Coordonne entre la View et le PaymentService.
 * 
 * Responsabilités :
 * - Recevoir les événements de la vue de paiement
 * - Valider les entrées utilisateur
 * - Appeler PaymentService
 * - Gérer les erreurs
 * - Mettre à jour la vue
 */
public class PaymentController {
    
    private PaymentService paymentService;
    private Utilisateur utilisateurConnecte; // L'utilisateur actuellement connecté
    private Admin adminConnecte; // L'admin actuellement connecté (pour validation)
    // TODO: Injecter la vue quand elle sera créée
    // private PaymentView view;
    
    /**
     * Constructeur avec injection du service.
     * 
     * @param paymentService Le service de paiement
     */
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Définit l'utilisateur connecté.
     * 
     * @param utilisateur L'utilisateur connecté
     */
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }
    
    /**
     * Définit l'admin connecté (pour validation des paiements).
     * 
     * @param admin L'admin connecté
     */
    public void setAdminConnecte(Admin admin) {
        this.adminConnecte = admin;
    }
    
    /**
     * Récupère l'utilisateur connecté.
     * 
     * @return L'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Gère le paiement d'une pénalité d'emprunt.
     * Appelé quand l'utilisateur clique sur "Payer la pénalité" dans la vue.
     * 
     * @param emprunt L'emprunt avec pénalité
     * @param methodePaiement La méthode de paiement (ESPECES, CARTE, CHEQUE, VIREMENT)
     */
    public void payerPenalite(Emprunt emprunt, String methodePaiement) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (emprunt == null) {
            // TODO: view.showError("Veuillez sélectionner un emprunt");
            return;
        }
        if (methodePaiement == null || methodePaiement.isEmpty()) {
            // TODO: view.showError("Veuillez sélectionner une méthode de paiement");
            return;
        }
        
        // 2. Vérifier que l'emprunt appartient à l'utilisateur
        if (emprunt.getUtilisateur() == null || 
            emprunt.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cet emprunt ne vous appartient pas");
            return;
        }
        
        // 3. Déléguer au Service
        try {
            Paiement paiement = paymentService.payerPenalite(utilisateurConnecte, emprunt, methodePaiement);
            
            // 4. Mettre à jour la vue
            // TODO: view.showSuccess("Paiement effectué avec succès. Référence: " + paiement.getReference());
            // TODO: view.afficherDetailsPaiement(paiement);
            // TODO: view.rafraichirSolde();
            
        } catch (IllegalArgumentException e) {
            // TODO: view.showError(e.getMessage());
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du paiement : " + e.getMessage());
        }
    }
    
    /**
     * Gère un paiement général (non lié à un emprunt).
     * 
     * @param montant Le montant à payer
     * @param methodePaiement La méthode de paiement
     * @param motif Le motif du paiement
     */
    public void effectuerPaiement(double montant, String methodePaiement, String motif) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (montant <= 0) {
            // TODO: view.showError("Le montant doit être positif");
            return;
        }
        if (methodePaiement == null || methodePaiement.isEmpty()) {
            // TODO: view.showError("Veuillez sélectionner une méthode de paiement");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            Paiement paiement = paymentService.effectuerPaiement(
                utilisateurConnecte, 
                montant, 
                methodePaiement, 
                motif
            );
            
            // 3. Mettre à jour la vue
            // TODO: view.showSuccess("Paiement effectué avec succès. Référence: " + paiement.getReference());
            // TODO: view.afficherDetailsPaiement(paiement);
            // TODO: view.rafraichirSolde();
            
        } catch (IllegalArgumentException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du paiement : " + e.getMessage());
        }
    }
    
    /**
     * Valide un paiement (pour les admins).
     * 
     * @param paiement Le paiement à valider
     */
    public void validerPaiement(Paiement paiement) {
        // 1. Validation
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (paiement == null) {
            // TODO: view.showError("Veuillez sélectionner un paiement");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            boolean success = paymentService.validerPaiement(paiement);
            if (success) {
                // TODO: view.showSuccess("Paiement validé avec succès");
                // TODO: view.rafraichirListePaiements();
            } else {
                // TODO: view.showError("Échec de la validation");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la validation : " + e.getMessage());
        }
    }
    
    /**
     * Annule un paiement (pour les admins).
     * 
     * @param paiement Le paiement à annuler
     */
    public void annulerPaiement(Paiement paiement) {
        // 1. Validation
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (paiement == null) {
            // TODO: view.showError("Veuillez sélectionner un paiement");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            boolean success = paymentService.annulerPaiement(paiement);
            if (success) {
                // TODO: view.showSuccess("Paiement annulé avec succès");
                // TODO: view.rafraichirListePaiements();
            } else {
                // TODO: view.showError("Échec de l'annulation");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de l'annulation : " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les paiements de l'utilisateur connecté.
     * 
     * @return Liste des paiements
     */
    public List<Paiement> getMesPaiements() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return List.of();
        }
        
        try {
            List<Paiement> paiements = paymentService.getPaiementsUtilisateur(utilisateurConnecte);
            // TODO: view.afficherPaiements(paiements);
            return paiements;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les paiements en attente de l'utilisateur connecté.
     * 
     * @return Liste des paiements en attente
     */
    public List<Paiement> getPaiementsEnAttente() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            return paymentService.getPaiementsEnAttente(utilisateurConnecte);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Calcule le total payé par l'utilisateur connecté.
     * 
     * @return Le montant total payé
     */
    public double getTotalPaye() {
        if (utilisateurConnecte == null) {
            return 0.0;
        }
        
        try {
            return paymentService.calculerTotalPaye(utilisateurConnecte);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du calcul : " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Récupère un paiement par sa référence.
     * 
     * @param reference La référence du paiement
     * @return Le paiement trouvé
     */
    public Optional<Paiement> getPaiementParReference(String reference) {
        if (reference == null || reference.isEmpty()) {
            return Optional.empty();
        }
        
        try {
            return paymentService.getPaiementParReference(reference);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return Optional.empty();
        }
    }
}

