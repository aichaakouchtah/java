package com.infinitpages.model.service;

import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.SuperAdmin;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.dao.PersonneDAO;
import com.infinitpages.model.dao.AdminDAO;
import com.infinitpages.model.dao.SuperAdminDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Service d'authentification pour les utilisateurs du système.
 * Gère la connexion et la vérification des identifiants.
 * 
 * Responsabilités :
 * - Authentification des utilisateurs (Admin, SuperAdmin, Utilisateur)
 * - Vérification des identifiants
 * - Gestion de session
 */
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private PersonneDAO personneDAO;
    private AdminDAO adminDAO;
    private SuperAdminDAO superAdminDAO;
    // TODO: Injecter UtilisateurDAO quand il sera créé
    // private UtilisateurDAO utilisateurDAO;
    
    /**
     * Constructeur par défaut.
     */
    public AuthService() {
        this.personneDAO = new PersonneDAO();
        this.adminDAO = new AdminDAO();
        this.superAdminDAO = new SuperAdminDAO();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param personneDAO Le DAO Personne à utiliser
     * @param adminDAO Le DAO Admin à utiliser
     * @param superAdminDAO Le DAO SuperAdmin à utiliser
     */
    public AuthService(PersonneDAO personneDAO, AdminDAO adminDAO, SuperAdminDAO superAdminDAO) {
        this.personneDAO = personneDAO;
        this.adminDAO = adminDAO;
        this.superAdminDAO = superAdminDAO;
    }
    
    /**
     * Authentifie un utilisateur avec son email et mot de passe.
     * 
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe (en clair, sera hashé)
     * @return La personne authentifiée (Admin, SuperAdmin ou Utilisateur)
     * @throws IllegalArgumentException Si les identifiants sont invalides
     * @throws IllegalStateException Si l'utilisateur n'existe pas ou est désactivé
     */
    public Personne seConnecter(String email, String motDePasse) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email est requis");
        }
        if (motDePasse == null || motDePasse.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est requis");
        }
        
        try {
            // Récupérer l'utilisateur depuis la base de données
            // On essaie d'abord SuperAdmin, puis Admin, puis Utilisateur
            SuperAdmin superAdmin = superAdminDAO.findByEmail(email);
            if (superAdmin != null) {
                // TODO: Vérifier le mot de passe (hashé)
                // if (!PasswordHasher.verify(motDePasse, superAdmin.getMotDePasse())) {
                //     throw new IllegalStateException("Email ou mot de passe incorrect");
                // }
                if (!motDePasse.equals(superAdmin.getMotDePasse())) {
                    throw new IllegalStateException("Email ou mot de passe incorrect");
                }
                
                if (!superAdmin.isEstActif()) {
                    throw new IllegalStateException("Ce compte est désactivé");
                }
                
                return superAdmin;
            }
            
            Admin admin = adminDAO.findByEmail(email);
            if (admin != null) {
                // TODO: Vérifier le mot de passe (hashé)
                if (!motDePasse.equals(admin.getMotDePasse())) {
                    throw new IllegalStateException("Email ou mot de passe incorrect");
                }
                
                if (!admin.isEstActif()) {
                    throw new IllegalStateException("Ce compte est désactivé");
                }
                
                return admin;
            }
            
            // TODO: Chercher dans UtilisateurDAO quand il sera créé
            // Utilisateur utilisateur = utilisateurDAO.findByEmail(email);
            // if (utilisateur != null) { ... }
            
            throw new IllegalStateException("Email ou mot de passe incorrect");
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'authentification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Authentifie un Admin.
     * 
     * @param email L'email de l'admin
     * @param motDePasse Le mot de passe
     * @return L'admin authentifié
     * @throws IllegalStateException Si l'admin n'existe pas ou n'est pas valide
     */
    public Admin seConnecterAdmin(String email, String motDePasse) {
        Personne personne = seConnecter(email, motDePasse);
        
        if (personne == null) {
            throw new IllegalStateException("Email ou mot de passe incorrect");
        }
        
        if (!(personne instanceof Admin)) {
            throw new IllegalStateException("Cet utilisateur n'est pas un administrateur");
        }
        
        Admin admin = (Admin) personne;
        
        // Vérifier que l'admin est valide
        if (!admin.estValide()) {
            throw new IllegalStateException("L'admin n'est pas valide (inactif ou sans permissions)");
        }
        
        return admin;
    }
    
    /**
     * Authentifie un SuperAdmin.
     * 
     * @param email L'email du super-admin
     * @param motDePasse Le mot de passe
     * @return Le super-admin authentifié
     * @throws IllegalStateException Si le super-admin n'existe pas
     */
    public SuperAdmin seConnecterSuperAdmin(String email, String motDePasse) {
        Personne personne = seConnecter(email, motDePasse);
        
        if (personne == null) {
            throw new IllegalStateException("Email ou mot de passe incorrect");
        }
        
        if (!(personne instanceof SuperAdmin)) {
            throw new IllegalStateException("Cet utilisateur n'est pas un super-administrateur");
        }
        
        return (SuperAdmin) personne;
    }
    
    /**
     * Authentifie un Utilisateur.
     * 
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe
     * @return L'utilisateur authentifié
     * @throws IllegalStateException Si l'utilisateur n'existe pas
     */
    public Utilisateur seConnecterUtilisateur(String email, String motDePasse) {
        Personne personne = seConnecter(email, motDePasse);
        
        if (personne == null) {
            throw new IllegalStateException("Email ou mot de passe incorrect");
        }
        
        if (!(personne instanceof Utilisateur)) {
            throw new IllegalStateException("Cet utilisateur n'est pas un utilisateur standard");
        }
        
        return (Utilisateur) personne;
    }
    
    /**
     * Vérifie si un email existe déjà dans le système.
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe déjà
     */
    public boolean emailExiste(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        try {
            // Vérifier dans la base de données
            return personneDAO.emailExists(email);
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification de l'email: {}", email, e);
            return false;
        }
    }
    
    /**
     * Déconnecte un utilisateur (pour l'instant, juste une méthode utilitaire).
     * 
     * @param personne La personne à déconnecter
     */
    public void seDeconnecter(Personne personne) {
        if (personne != null) {
            // TODO: Nettoyer la session, logger la déconnexion, etc.
            // sessionManager.clearSession(personne);
        }
    }
    
    /**
     * Vérifie si un utilisateur est connecté et actif.
     * 
     * @param personne La personne à vérifier
     * @return true si l'utilisateur est connecté et actif
     */
    public boolean estConnecteEtActif(Personne personne) {
        if (personne == null) {
            return false;
        }
        
        if (!personne.isEstActif()) {
            return false;
        }
        
        // Vérifications spécifiques selon le type
        if (personne instanceof Admin) {
            Admin admin = (Admin) personne;
            return admin.estValide();
        }
        
        return true;
    }
}

