package com.infinitpages.controller;

import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Avis;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.AvisDAO;
import com.infinitpages.model.dao.impl.UtilisateurDAOImpl;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;
import com.infinitpages.model.dao.impl.AvisDAOImpl;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion du profil utilisateur.
 * Coordonne entre la View et les DAO.
 * 
 * Responsabilités :
 * - Gestion du profil utilisateur
 * - Consultation des emprunts
 * - Consultation de l'historique
 * - Gestion des avis
 * - Consultation du solde à payer
 */
public class UtilisateurController {
    
    private UtilisateurDAO utilisateurDAO;
    private EmpruntDAO empruntDAO;
    private AvisDAO avisDAO;
    private Utilisateur utilisateurConnecte; // L'utilisateur actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private UtilisateurView view;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurController() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl();
        this.avisDAO = new AvisDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param utilisateurDAO Le DAO Utilisateur à utiliser
     * @param empruntDAO Le DAO Emprunt à utiliser
     * @param avisDAO Le DAO Avis à utiliser
     */
    public UtilisateurController(UtilisateurDAO utilisateurDAO, EmpruntDAO empruntDAO, AvisDAO avisDAO) {
        this.utilisateurDAO = utilisateurDAO;
        this.empruntDAO = empruntDAO;
        this.avisDAO = avisDAO;
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
     * Récupère l'utilisateur connecté.
     * 
     * @return L'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Récupère le profil de l'utilisateur connecté.
     * 
     * @return L'utilisateur avec ses informations
     */
    public Utilisateur getProfil() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return null;
        }
        
        try {
            Optional<Utilisateur> utilisateurOpt = utilisateurDAO.findById(utilisateurConnecte.getId());
            if (utilisateurOpt.isPresent()) {
                utilisateurConnecte = utilisateurOpt.get();
                // TODO: view.afficherProfil(utilisateurConnecte);
                return utilisateurConnecte;
            }
            return null;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération du profil : " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Met à jour le profil de l'utilisateur.
     * 
     * @param utilisateur L'utilisateur avec les nouvelles informations
     */
    public void mettreAJourProfil(Utilisateur utilisateur) {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        
        if (utilisateur == null) {
            // TODO: view.showError("Les informations sont invalides");
            return;
        }
        
        // S'assurer que l'ID correspond
        utilisateur.setId(utilisateurConnecte.getId());
        
        try {
            boolean success = utilisateurDAO.update(utilisateur);
            if (success) {
                utilisateurConnecte = utilisateur;
                // TODO: view.showSuccess("Profil mis à jour avec succès");
                // TODO: view.afficherProfil(utilisateurConnecte);
            } else {
                // TODO: view.showError("Échec de la mise à jour du profil");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les emprunts de l'utilisateur connecté.
     * 
     * @return Liste des emprunts
     */
    public List<Emprunt> getMesEmprunts() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return List.of();
        }
        
        try {
            List<Emprunt> emprunts = empruntDAO.findByUtilisateur(utilisateurConnecte.getId());
            // TODO: view.afficherEmprunts(emprunts);
            return emprunts;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les emprunts actifs de l'utilisateur connecté.
     * 
     * @return Liste des emprunts actifs
     */
    public List<Emprunt> getMesEmpruntsActifs() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            return empruntDAO.findActifsByUtilisateur(utilisateurConnecte.getId());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les emprunts en retard de l'utilisateur connecté.
     * 
     * @return Liste des emprunts en retard
     */
    public List<Emprunt> getMesEmpruntsEnRetard() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            return empruntDAO.findEnRetardByUtilisateur(utilisateurConnecte.getId());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère le solde à payer de l'utilisateur connecté.
     * 
     * @return Le solde à payer
     */
    public double getSoldeAPayer() {
        if (utilisateurConnecte == null) {
            return 0.0;
        }
        
        try {
            Optional<Utilisateur> utilisateurOpt = utilisateurDAO.findById(utilisateurConnecte.getId());
            if (utilisateurOpt.isPresent()) {
                return utilisateurOpt.get().getSoldeAPayer();
            }
            return 0.0;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Ajoute un avis sur un document.
     * 
     * @param document Le document
     * @param note La note (1-5)
     * @param commentaire Le commentaire
     */
    public void ajouterAvis(Document document, int note, String commentaire) {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        
        if (document == null) {
            // TODO: view.showError("Document invalide");
            return;
        }
        
        if (note < 1 || note > 5) {
            // TODO: view.showError("La note doit être entre 1 et 5");
            return;
        }
        
        try {
            // Vérifier si l'utilisateur a déjà laissé un avis
            Optional<Avis> avisExistant = avisDAO.findByUtilisateurAndDocument(
                utilisateurConnecte.getId(), 
                document.getId()
            );
            
            if (avisExistant.isPresent()) {
                // TODO: view.showError("Vous avez déjà laissé un avis sur ce document");
                return;
            }
            
            // Créer un nouvel avis
            Avis avis = new Avis(utilisateurConnecte, document, note, commentaire);
            avisDAO.save(avis);
            
            // TODO: view.showSuccess("Avis ajouté avec succès");
            // TODO: view.rafraichirAvis(document);
            
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de l'ajout de l'avis : " + e.getMessage());
        }
    }
    
    /**
     * Récupère les avis de l'utilisateur connecté.
     * 
     * @return Liste des avis
     */
    public List<Avis> getMesAvis() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            return avisDAO.findByUtilisateur(utilisateurConnecte.getId());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
}

