package com.infinitpages.controller;

import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.SuperAdmin;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.service.AuthService;

/**
 * Contrôleur pour l'authentification.
 * Coordonne entre la View et le AuthService.
 * 
 * Responsabilités :
 * - Gérer la connexion (Admin, SuperAdmin, Utilisateur)
 * - Gérer la déconnexion
 * - Vérifier si un email existe
 * - Valider les entrées utilisateur
 * - Gérer les erreurs d'authentification
 */
public class AuthController {
    
    private AuthService authService;
    private Personne utilisateurConnecte; // L'utilisateur actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private LoginView view;
    
    /**
     * Constructeur avec injection du service.
     * 
     * @param authService Le service d'authentification
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Récupère l'utilisateur connecté.
     * 
     * @return L'utilisateur connecté (Admin, SuperAdmin ou Utilisateur)
     */
    public Personne getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Vérifie si un utilisateur est connecté.
     * 
     * @return true si un utilisateur est connecté
     */
    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    /**
     * Gère la connexion d'un utilisateur.
     * Appelé quand l'utilisateur clique sur "Se connecter" dans la vue.
     * 
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe
     * @return true si la connexion a réussi
     */
    public boolean seConnecter(String email, String motDePasse) {
        // 1. Validation des entrées
        if (email == null || email.isEmpty()) {
            // TODO: view.showError("L'email est requis");
            return false;
        }
        if (motDePasse == null || motDePasse.isEmpty()) {
            // TODO: view.showError("Le mot de passe est requis");
            return false;
        }
        
        // 2. Déléguer au Service
        try {
            Personne personne = authService.seConnecter(email, motDePasse);
            utilisateurConnecte = personne;
            
            // 3. Rediriger selon le type d'utilisateur
            if (personne instanceof SuperAdmin) {
                // TODO: view.naviguerVersSuperAdminDashboard((SuperAdmin) personne);
                return true;
            } else if (personne instanceof Admin) {
                // TODO: view.naviguerVersAdminDashboard((Admin) personne);
                return true;
            } else if (personne instanceof Utilisateur) {
                // TODO: view.naviguerVersUtilisateurDashboard((Utilisateur) personne);
                return true;
            }
            
            return false;
            
        } catch (IllegalArgumentException e) {
            // TODO: view.showError(e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            // TODO: view.showError(e.getMessage());
            return false;
        } catch (Exception e) {
            // TODO: view.showError("Une erreur est survenue lors de la connexion : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gère la connexion d'un Admin.
     * 
     * @param email L'email de l'admin
     * @param motDePasse Le mot de passe
     * @return L'admin connecté, ou null si échec
     */
    public Admin seConnecterAdmin(String email, String motDePasse) {
        try {
            Admin admin = authService.seConnecterAdmin(email, motDePasse);
            utilisateurConnecte = admin;
            // TODO: view.naviguerVersAdminDashboard(admin);
            return admin;
        } catch (Exception e) {
            // TODO: view.showError(e.getMessage());
            return null;
        }
    }
    
    /**
     * Gère la connexion d'un SuperAdmin.
     * 
     * @param email L'email du super-admin
     * @param motDePasse Le mot de passe
     * @return Le super-admin connecté, ou null si échec
     */
    public SuperAdmin seConnecterSuperAdmin(String email, String motDePasse) {
        try {
            SuperAdmin superAdmin = authService.seConnecterSuperAdmin(email, motDePasse);
            utilisateurConnecte = superAdmin;
            // TODO: view.naviguerVersSuperAdminDashboard(superAdmin);
            return superAdmin;
        } catch (Exception e) {
            // TODO: view.showError(e.getMessage());
            return null;
        }
    }
    
    /**
     * Gère la connexion d'un Utilisateur.
     * 
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe
     * @return L'utilisateur connecté, ou null si échec
     */
    public Utilisateur seConnecterUtilisateur(String email, String motDePasse) {
        try {
            Utilisateur utilisateur = authService.seConnecterUtilisateur(email, motDePasse);
            utilisateurConnecte = utilisateur;
            // TODO: view.naviguerVersUtilisateurDashboard(utilisateur);
            return utilisateur;
        } catch (Exception e) {
            // TODO: view.showError(e.getMessage());
            return null;
        }
    }
    
    /**
     * Gère la déconnexion.
     * Appelé quand l'utilisateur clique sur "Se déconnecter".
     */
    public void seDeconnecter() {
        if (utilisateurConnecte != null) {
            authService.seDeconnecter(utilisateurConnecte);
            utilisateurConnecte = null;
            // TODO: view.naviguerVersLogin();
            // TODO: view.showSuccess("Déconnexion réussie");
        }
    }
    
    /**
     * Vérifie si un email existe déjà dans le système.
     * Utile pour la création de compte.
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe déjà
     */
    public boolean emailExiste(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        return authService.emailExiste(email);
    }
    
    /**
     * Vérifie si l'utilisateur connecté est actif.
     * 
     * @return true si l'utilisateur est connecté et actif
     */
    public boolean estConnecteEtActif() {
        if (utilisateurConnecte == null) {
            return false;
        }
        
        return authService.estConnecteEtActif(utilisateurConnecte);
    }
}

