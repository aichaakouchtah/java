package com.infinitpages.controller;

import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Avis;
import com.infinitpages.model.entity.Rapport;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.service.AdminService;

import java.util.List;

/**
 * Contrôleur pour la gestion administrative.
 * Coordonne entre la View et le AdminService.
 * 
 * Responsabilités :
 * - Recevoir les événements de la vue admin
 * - Valider les entrées utilisateur
 * - Appeler les services
 * - Gérer les erreurs
 * - Mettre à jour la vue
 */
public class AdminController {
    
    private AdminService adminService;
    private Admin adminConnecte; // L'admin actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private AdminView view;
    
    /**
     * Constructeur avec injection du service.
     * 
     * @param adminService Le service administratif
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * Définit l'admin connecté.
     * 
     * @param admin L'admin connecté
     */
    public void setAdminConnecte(Admin admin) {
        this.adminConnecte = admin;
    }
    
    /**
     * Récupère l'admin connecté.
     * 
     * @return L'admin connecté
     */
    public Admin getAdminConnecte() {
        return adminConnecte;
    }
    
    /**
     * Gère l'action d'ajouter un document.
     * Appelé quand l'admin clique sur "Ajouter Document" dans la vue.
     * 
     * @param document Le document à ajouter
     */
    public void ajouterDocument(Document document) {
        // 1. Validation
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (document == null) {
            // TODO: view.showError("Veuillez remplir tous les champs du document");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            adminService.ajouterDocument(adminConnecte, document);
            // TODO: view.showSuccess("Document ajouté avec succès");
            // TODO: view.rafraichirListeDocuments();
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Une erreur est survenue : " + e.getMessage());
        }
    }
    
    /**
     * Gère l'action de modifier un document.
     * 
     * @param document Le document modifié
     */
    public void modifierDocument(Document document) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (document == null) {
            // TODO: view.showError("Veuillez sélectionner un document");
            return;
        }
        
        try {
            adminService.modifierDocument(adminConnecte, document);
            // TODO: view.showSuccess("Document modifié avec succès");
            // TODO: view.rafraichirListeDocuments();
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la modification : " + e.getMessage());
        }
    }
    
    /**
     * Gère l'action de supprimer un document.
     * 
     * @param documentId L'identifiant du document à supprimer
     */
    public void supprimerDocument(int documentId) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        
        try {
            adminService.supprimerDocument(adminConnecte, documentId);
            // TODO: view.showSuccess("Document supprimé avec succès");
            // TODO: view.rafraichirListeDocuments();
            
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la suppression : " + e.getMessage());
        }
    }
    
    /**
     * Gère les catégories (ajout, modification, suppression).
     * 
     * @param categorie La catégorie à gérer
     */
    public void gererCategories(Categorie categorie) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (categorie == null) {
            // TODO: view.showError("Veuillez remplir tous les champs de la catégorie");
            return;
        }
        
        try {
            adminService.gererCategories(adminConnecte, categorie);
            // TODO: view.showSuccess("Catégorie gérée avec succès");
            // TODO: view.rafraichirListeCategories();
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Génère un rapport sur les emprunts.
     * 
     * @param periode La période couverte (ex: "Janvier 2025")
     */
    public void genererRapportEmprunts(String periode) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (periode == null || periode.isEmpty()) {
            // TODO: view.showError("Veuillez spécifier une période");
            return;
        }
        
        try {
            Rapport rapport = adminService.genererRapportEmprunts(adminConnecte, periode);
            // TODO: view.afficherRapport(rapport);
            // TODO: view.showSuccess("Rapport généré avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }
    
    /**
     * Génère un rapport sur les consultations.
     * 
     * @param periode La période couverte
     */
    public void genererRapportConsultations(String periode) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (periode == null || periode.isEmpty()) {
            // TODO: view.showError("Veuillez spécifier une période");
            return;
        }
        
        try {
            Rapport rapport = adminService.genererRapportConsultations(adminConnecte, periode);
            // TODO: view.afficherRapport(rapport);
            // TODO: view.showSuccess("Rapport généré avec succès");
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }
    
    /**
     * Affiche l'activité des utilisateurs.
     */
    public void suivreActiviteUtilisateurs() {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        
        try {
            List<Utilisateur> utilisateurs = adminService.suivreActiviteUtilisateurs(adminConnecte);
            // TODO: view.afficherActiviteUtilisateurs(utilisateurs);
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Affiche les emprunts avec pénalités.
     */
    public void gererPenalites() {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        
        try {
            List<Emprunt> empruntsAvecPenalites = adminService.gererPenalites(adminConnecte);
            // TODO: view.afficherPenalites(empruntsAvecPenalites);
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Valide le retour d'un document emprunté.
     * 
     * @param emprunt L'emprunt à valider
     */
    public void validerRetour(Emprunt emprunt) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (emprunt == null) {
            // TODO: view.showError("Veuillez sélectionner un emprunt");
            return;
        }
        
        try {
            adminService.validerRetour(adminConnecte, emprunt);
            // TODO: view.showSuccess("Retour validé avec succès");
            // TODO: view.rafraichirListeEmprunts();
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la validation : " + e.getMessage());
        }
    }
    
    /**
     * Modère un avis (approuve ou rejette).
     * 
     * @param avis L'avis à modérer
     * @param approuver true pour approuver, false pour rejeter
     */
    public void modererAvis(Avis avis, boolean approuver) {
        if (adminConnecte == null) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (avis == null) {
            // TODO: view.showError("Veuillez sélectionner un avis");
            return;
        }
        
        try {
            adminService.modererAvis(adminConnecte, avis, approuver);
            String message = approuver ? "Avis approuvé avec succès" : "Avis rejeté avec succès";
            // TODO: view.showSuccess(message);
            // TODO: view.rafraichirListeAvis();
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la modération : " + e.getMessage());
        }
    }
}

