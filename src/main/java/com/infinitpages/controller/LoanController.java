package com.infinitpages.controller;

import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.service.LoanService;

/**
 * Contrôleur pour la gestion des emprunts.
 * Coordonne entre la View et le Service.
 * 
 * Responsabilités :
 * - Recevoir les événements de la vue
 * - Valider les entrées utilisateur (format, null)
 * - Appeler les services
 * - Gérer les erreurs
 * - Mettre à jour la vue
 */
public class LoanController {
    
    private LoanService loanService;
    // TODO: Injecter la vue quand elle sera créée
    // private LoanView view;
    
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }
    
    /**
     * Gère l'action d'emprunter un document.
     * Appelé quand l'utilisateur clique sur "Emprunter" dans la vue.
     * 
     * @param utilisateur L'utilisateur qui emprunte
     * @param document Le document à emprunter
     */
    public void emprunterDocument(Utilisateur utilisateur, Document document) {
        // 1. Validation simple des entrées (format, null)
        if (utilisateur == null) {
            // TODO: view.showError("Utilisateur non connecté");
            return;
        }
        if (document == null) {
            // TODO: view.showError("Veuillez sélectionner un document");
            return;
        }
        
        // 2. Déléguer la logique métier au Service
        try {
            Emprunt emprunt = loanService.emprunterDocument(utilisateur, document);
            
            // 3. Récupérer les informations calculées (par l'entité)
            double prixTotal = emprunt.calculerPrixTotal();
            int joursGratuits = emprunt.getJoursGratuitsApplicables();
            
            // 4. Mettre à jour la vue avec les résultats
            // TODO: view.showSuccess("Document emprunté avec succès");
            // TODO: view.afficherDetailsEmprunt(emprunt);
            // TODO: view.afficherPrixTotal(prixTotal);
            // TODO: view.afficherJoursGratuits(joursGratuits);
            
        } catch (IllegalStateException e) {
            // 5. Gérer les erreurs métier
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // 6. Gérer les erreurs inattendues
            // TODO: view.showError("Une erreur est survenue : " + e.getMessage());
        }
    }
    
    /**
     * Gère l'action de retourner un document.
     * Appelé quand l'utilisateur clique sur "Retourner" dans la vue.
     * 
     * @param emprunt L'emprunt à retourner
     */
    public void retournerDocument(Emprunt emprunt) {
        // 1. Validation simple
        if (emprunt == null) {
            // TODO: view.showError("Veuillez sélectionner un emprunt");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            Emprunt empruntRetourne = loanService.retournerDocument(emprunt);
            
            // 3. Récupérer les calculs (faits par l'entité)
            double montantTotal = empruntRetourne.calculerMontantTotal();
            double penalite = empruntRetourne.calculerPenalite();
            long joursRetard = empruntRetourne.calculerJoursRetard();
            
            // 4. Mettre à jour la vue
            // TODO: view.showSuccess("Document retourné avec succès");
            // TODO: view.afficherMontantTotal(montantTotal);
            
            if (joursRetard > 0) {
                // TODO: view.afficherPenalite(penalite);
                // TODO: view.afficherJoursRetard(joursRetard);
            }
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du retour : " + e.getMessage());
        }
    }
    
    /**
     * Affiche les détails d'un emprunt avec tous les calculs.
     * Les calculs sont faits par l'entité, le controller les récupère juste.
     * 
     * @param emprunt L'emprunt à afficher
     */
    public void afficherDetailsEmprunt(Emprunt emprunt) {
        if (emprunt == null) {
            return;
        }
        
        // Récupérer tous les calculs depuis l'entité
        double prixTotal = emprunt.calculerPrixTotal();
        double penalite = emprunt.calculerPenalite();
        double montantTotal = emprunt.calculerMontantTotal();
        int joursGratuits = emprunt.getJoursGratuitsApplicables();
        int joursFacturables = emprunt.calculerJoursFacturables();
        long joursRetard = emprunt.calculerJoursRetard();
        boolean enRetard = emprunt.estEnRetard();
        
        // Passer les données à la vue pour affichage
        // TODO: view.afficherDetails(emprunt, prixTotal, penalite, montantTotal, 
        //                            joursGratuits, joursFacturables, joursRetard, enRetard);
    }
}

