package com.infinitpages.controller;

import com.infinitpages.model.entity.Historique;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.dao.HistoriqueDAO;
import com.infinitpages.model.dao.impl.HistoriqueDAOImpl;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion de l'historique et des listes de lecture.
 * Coordonne entre la View et le HistoriqueDAO.
 * 
 * Responsabilités :
 * - Gestion de l'historique des actions
 * - Création et gestion des listes de lecture
 * - Partage de listes de lecture
 * - Ajout/Retrait de documents dans les listes
 */
public class HistoriqueController {
    
    private HistoriqueDAO historiqueDAO;
    private Utilisateur utilisateurConnecte; // L'utilisateur actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private HistoriqueView view;
    
    /**
     * Constructeur par défaut.
     */
    public HistoriqueController() {
        this.historiqueDAO = new HistoriqueDAOImpl();
    }
    
    /**
     * Constructeur avec injection du DAO (pour les tests).
     * 
     * @param historiqueDAO Le DAO Historique à utiliser
     */
    public HistoriqueController(HistoriqueDAO historiqueDAO) {
        this.historiqueDAO = historiqueDAO;
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
     * Récupère tout l'historique de l'utilisateur connecté.
     * 
     * @return Liste de tout l'historique (actions + listes de lecture)
     */
    public List<Historique> getMonHistorique() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return List.of();
        }
        
        try {
            List<Historique> historique = historiqueDAO.findByPersonne(utilisateurConnecte.getId());
            // TODO: view.afficherHistorique(historique);
            return historique;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération de l'historique : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère uniquement l'historique des actions (pas les listes de lecture).
     * 
     * @return Liste de l'historique des actions
     */
    public List<Historique> getHistoriqueActions() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            return historiqueDAO.findByPersonneAndType(utilisateurConnecte.getId(), "HISTORIQUE");
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère toutes les listes de lecture de l'utilisateur connecté.
     * 
     * @return Liste des listes de lecture
     */
    public List<Historique> getMesListesLecture() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return List.of();
        }
        
        try {
            List<Historique> listes = historiqueDAO.findByPersonneAndType(utilisateurConnecte.getId(), "LISTE_LECTURE");
            // TODO: view.afficherListesLecture(listes);
            return listes;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Crée une nouvelle liste de lecture.
     * 
     * @param titre Le titre de la liste
     * @param description La description (optionnelle)
     * @return La liste créée
     */
    public Historique creerListeLecture(String titre, String description) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return null;
        }
        if (titre == null || titre.isEmpty()) {
            // TODO: view.showError("Le titre est requis");
            return null;
        }
        
        // 2. Créer la liste
        try {
            Historique liste = new Historique("LISTE_LECTURE", titre, utilisateurConnecte);
            if (description != null && !description.isEmpty()) {
                liste.setDescription(description);
            }
            
            Historique savedListe = historiqueDAO.save(liste);
            // TODO: view.showSuccess("Liste de lecture créée avec succès");
            // TODO: view.rafraichirListesLecture();
            return savedListe;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la création : " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Modifie une liste de lecture existante.
     * 
     * @param liste La liste à modifier
     * @param nouveauTitre Le nouveau titre (peut être null)
     * @param nouvelleDescription La nouvelle description (peut être null)
     */
    public void modifierListeLecture(Historique liste, String nouveauTitre, String nouvelleDescription) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (liste == null) {
            // TODO: view.showError("Veuillez sélectionner une liste");
            return;
        }
        
        // 2. Vérifier que la liste appartient à l'utilisateur
        if (liste.getUtilisateur() == null || 
            liste.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette liste ne vous appartient pas");
            return;
        }
        
        // 3. Modifier
        try {
            if (nouveauTitre != null && !nouveauTitre.isEmpty()) {
                liste.setTitre(nouveauTitre);
            }
            if (nouvelleDescription != null) {
                liste.setDescription(nouvelleDescription);
            }
            liste.setDateModification(java.time.LocalDateTime.now());
            
            boolean success = historiqueDAO.update(liste);
            if (success) {
                // TODO: view.showSuccess("Liste modifiée avec succès");
                // TODO: view.rafraichirListesLecture();
            } else {
                // TODO: view.showError("Échec de la modification");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la modification : " + e.getMessage());
        }
    }
    
    /**
     * Supprime une liste de lecture.
     * 
     * @param liste La liste à supprimer
     */
    public void supprimerListeLecture(Historique liste) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (liste == null) {
            // TODO: view.showError("Veuillez sélectionner une liste");
            return;
        }
        
        // 2. Vérifier que la liste appartient à l'utilisateur
        if (liste.getUtilisateur() == null || 
            liste.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette liste ne vous appartient pas");
            return;
        }
        
        // 3. Supprimer
        try {
            boolean success = historiqueDAO.delete(liste.getId());
            if (success) {
                // TODO: view.showSuccess("Liste supprimée avec succès");
                // TODO: view.rafraichirListesLecture();
            } else {
                // TODO: view.showError("Échec de la suppression");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la suppression : " + e.getMessage());
        }
    }
    
    /**
     * Ajoute un document à une liste de lecture.
     * 
     * @param liste La liste de lecture
     * @param document Le document à ajouter
     */
    public void ajouterDocumentAListe(Historique liste, Document document) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (liste == null) {
            // TODO: view.showError("Veuillez sélectionner une liste");
            return;
        }
        if (document == null) {
            // TODO: view.showError("Veuillez sélectionner un document");
            return;
        }
        
        // 2. Vérifier que la liste appartient à l'utilisateur
        if (liste.getUtilisateur() == null || 
            liste.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette liste ne vous appartient pas");
            return;
        }
        
        // 3. Ajouter le document
        try {
            boolean success = historiqueDAO.addDocument(liste.getId(), document.getId());
            if (success) {
                // TODO: view.showSuccess("Document ajouté à la liste");
                // TODO: view.rafraichirListe(liste);
            } else {
                // TODO: view.showError("Le document est peut-être déjà dans la liste");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de l'ajout : " + e.getMessage());
        }
    }
    
    /**
     * Retire un document d'une liste de lecture.
     * 
     * @param liste La liste de lecture
     * @param document Le document à retirer
     */
    public void retirerDocumentDeListe(Historique liste, Document document) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (liste == null) {
            // TODO: view.showError("Veuillez sélectionner une liste");
            return;
        }
        if (document == null) {
            // TODO: view.showError("Veuillez sélectionner un document");
            return;
        }
        
        // 2. Vérifier que la liste appartient à l'utilisateur
        if (liste.getUtilisateur() == null || 
            liste.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette liste ne vous appartient pas");
            return;
        }
        
        // 3. Retirer le document
        try {
            boolean success = historiqueDAO.removeDocument(liste.getId(), document.getId());
            if (success) {
                // TODO: view.showSuccess("Document retiré de la liste");
                // TODO: view.rafraichirListe(liste);
            } else {
                // TODO: view.showError("Le document n'est pas dans cette liste");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du retrait : " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les documents d'une liste de lecture.
     * 
     * @param liste La liste de lecture
     * @return Liste des documents
     */
    public List<Document> getDocumentsDeListe(Historique liste) {
        if (liste == null) {
            return List.of();
        }
        
        try {
            return historiqueDAO.findDocumentsByHistorique(liste.getId());
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Partage ou ne partage plus une liste de lecture.
     * 
     * @param liste La liste de lecture
     * @param partager true pour partager, false pour ne plus partager
     */
    public void partagerListe(Historique liste, boolean partager) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (liste == null) {
            // TODO: view.showError("Veuillez sélectionner une liste");
            return;
        }
        
        // 2. Vérifier que la liste appartient à l'utilisateur
        if (liste.getUtilisateur() == null || 
            liste.getUtilisateur().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette liste ne vous appartient pas");
            return;
        }
        
        // 3. Modifier le statut de partage
        try {
            liste.setEstPartage(partager);
            liste.setDateModification(java.time.LocalDateTime.now());
            
            boolean success = historiqueDAO.update(liste);
            if (success) {
                String message = partager ? "Liste partagée avec succès" : "Liste n'est plus partagée";
                // TODO: view.showSuccess(message);
                // TODO: view.rafraichirListesLecture();
            } else {
                // TODO: view.showError("Échec de la mise à jour");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    
    /**
     * Récupère toutes les listes de lecture partagées (par tous les utilisateurs).
     * 
     * @return Liste des listes de lecture partagées
     */
    public List<Historique> getListesPartagees() {
        try {
            List<Historique> listes = historiqueDAO.findListesPartagees();
            // TODO: view.afficherListesPartagees(listes);
            return listes;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère une liste de lecture par son ID.
     * 
     * @param id L'ID de la liste
     * @return La liste trouvée
     */
    public Optional<Historique> getListeParId(int id) {
        try {
            return historiqueDAO.findById(id);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return Optional.empty();
        }
    }
}

