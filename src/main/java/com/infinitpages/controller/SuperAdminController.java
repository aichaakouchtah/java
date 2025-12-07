package com.infinitpages.controller;

import com.infinitpages.model.entity.SuperAdmin;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Rapport;
import com.infinitpages.model.service.SuperAdminService;
import com.infinitpages.util.constants.TypeAdmin;
import com.infinitpages.util.constants.TypeUtilisateur;

import java.util.List;

/**
 * Contrôleur pour la gestion du Super Administrateur.
 * Coordonne entre la View et le SuperAdminService.
 * 
 * Responsabilités :
 * - Recevoir les événements de la vue super-admin
 * - Valider les entrées utilisateur
 * - Appeler les services
 * - Gérer les erreurs
 * - Mettre à jour la vue
 */
public class SuperAdminController {
    
    private SuperAdminService superAdminService;
    private SuperAdmin superAdminConnecte; // Le super-admin actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private SuperAdminView view;
    
    /**
     * Constructeur avec injection du service.
     * 
     * @param superAdminService Le service super-admin
     */
    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }
    
    /**
     * Définit le super-admin connecté.
     * 
     * @param superAdmin Le super-admin connecté
     */
    public void setSuperAdminConnecte(SuperAdmin superAdmin) {
        this.superAdminConnecte = superAdmin;
    }
    
    /**
     * Récupère le super-admin connecté.
     * 
     * @return Le super-admin connecté
     */
    public SuperAdmin getSuperAdminConnecte() {
        return superAdminConnecte;
    }
    
    /**
     * Gère l'action de créer un nouvel administrateur.
     * 
     * @param admin Le nouvel admin à créer
     */
    public void creerAdmin(Admin admin) {
        // 1. Validation
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (admin == null) {
            // TODO: view.showError("Veuillez remplir tous les champs");
            return;
        }
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) {
            // TODO: view.showError("L'email est requis");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            superAdminService.creerAdmin(superAdminConnecte, admin);
            // TODO: view.showSuccess("Admin créé avec succès");
            // TODO: view.rafraichirListeAdmins();
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Une erreur est survenue : " + e.getMessage());
        }
    }
    
    /**
     * Gère l'action de supprimer un administrateur.
     * 
     * @param admin L'admin à supprimer
     */
    public void supprimerAdmin(Admin admin) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (admin == null) {
            // TODO: view.showError("Veuillez sélectionner un admin");
            return;
        }
        
        try {
            superAdminService.supprimerAdmin(superAdminConnecte, admin);
            // TODO: view.showSuccess("Admin supprimé avec succès");
            // TODO: view.rafraichirListeAdmins();
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la suppression : " + e.getMessage());
        }
    }
    
    /**
     * Donne des permissions à un admin.
     * 
     * @param admin L'admin à qui donner les permissions
     * @param typeAdmin Le nouveau type d'admin (permissions)
     */
    public void givePermissionsToAdmin(Admin admin, TypeAdmin typeAdmin) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (admin == null) {
            // TODO: view.showError("Veuillez sélectionner un admin");
            return;
        }
        if (typeAdmin == null) {
            // TODO: view.showError("Veuillez sélectionner un type d'admin");
            return;
        }
        
        try {
            superAdminService.givePermissionsToAdmin(superAdminConnecte, admin, typeAdmin);
            // TODO: view.showSuccess("Permissions mises à jour avec succès");
            // TODO: view.rafraichirListeAdmins();
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Affiche tous les utilisateurs du système.
     */
    public void voirTousLesUtilisateurs() {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        
        try {
            List<Utilisateur> utilisateurs = superAdminService.voirTousLesUtilisateurs(superAdminConnecte);
            // TODO: view.afficherUtilisateurs(utilisateurs);
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Génère un rapport global sur tout le système.
     * 
     * @param periode La période couverte (ex: "Janvier 2025")
     */
    public void genererRapportGlobal(String periode) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (periode == null || periode.isEmpty()) {
            // TODO: view.showError("Veuillez spécifier une période");
            return;
        }
        
        try {
            Rapport rapport = superAdminService.genererRapportGlobal(superAdminConnecte, periode);
            // TODO: view.afficherRapport(rapport);
            // TODO: view.showSuccess("Rapport global généré avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }
    
    /**
     * Configure un paramètre système global.
     * 
     * @param parametre Le nom du paramètre
     * @param valeur La nouvelle valeur
     */
    public void configurerSysteme(String parametre, String valeur) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (parametre == null || parametre.isEmpty()) {
            // TODO: view.showError("Le nom du paramètre est requis");
            return;
        }
        if (valeur == null || valeur.isEmpty()) {
            // TODO: view.showError("La valeur est requise");
            return;
        }
        
        try {
            superAdminService.configurerSysteme(superAdminConnecte, parametre, valeur);
            // TODO: view.showSuccess("Configuration mise à jour avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Définit les tarifs des pénalités.
     * 
     * @param tarifParJour Le tarif par jour de retard
     */
    public void definirTarifsPenalites(double tarifParJour) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (tarifParJour < 0) {
            // TODO: view.showError("Le tarif ne peut pas être négatif");
            return;
        }
        
        try {
            superAdminService.definirTarifsPenalites(superAdminConnecte, tarifParJour);
            // TODO: view.showSuccess("Tarif des pénalités mis à jour avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Modifie les durées d'emprunt par défaut pour un type d'utilisateur.
     * 
     * @param typeUtilisateur Le type d'utilisateur
     * @param dureeJours La nouvelle durée en jours
     */
    public void modifierDureesEmprunt(TypeUtilisateur typeUtilisateur, int dureeJours) {
        if (superAdminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant que super-admin");
            return;
        }
        if (typeUtilisateur == null) {
            // TODO: view.showError("Veuillez sélectionner un type d'utilisateur");
            return;
        }
        if (dureeJours <= 0) {
            // TODO: view.showError("La durée doit être positive");
            return;
        }
        
        try {
            superAdminService.modifierDureesEmprunt(superAdminConnecte, typeUtilisateur, dureeJours);
            // TODO: view.showSuccess("Durée d'emprunt mise à jour avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
}

