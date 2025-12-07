package com.infinitpages.controller;

import com.infinitpages.model.entity.Notification;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.service.NotificationService;

import java.util.List;

/**
 * Contrôleur pour la gestion des notifications.
 * Coordonne entre la View et le NotificationService.
 * 
 * Responsabilités :
 * - Recevoir les événements de la vue de notifications
 * - Valider les entrées utilisateur
 * - Appeler NotificationService
 * - Gérer les erreurs
 * - Mettre à jour la vue
 */
public class NotificationController {
    
    private NotificationService notificationService;
    private Personne utilisateurConnecte; // L'utilisateur actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private NotificationView view;
    
    /**
     * Constructeur avec injection du service.
     * 
     * @param notificationService Le service de notification
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Définit l'utilisateur connecté.
     * 
     * @param personne La personne connectée
     */
    public void setUtilisateurConnecte(Personne personne) {
        this.utilisateurConnecte = personne;
    }
    
    /**
     * Récupère l'utilisateur connecté.
     * 
     * @return L'utilisateur connecté
     */
    public Personne getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Récupère toutes les notifications de l'utilisateur connecté.
     * 
     * @return Liste des notifications
     */
    public List<Notification> getMesNotifications() {
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return List.of();
        }
        
        try {
            List<Notification> notifications = notificationService.getNotifications(utilisateurConnecte);
            // TODO: view.afficherNotifications(notifications);
            return notifications;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les notifications non lues de l'utilisateur connecté.
     * 
     * @return Liste des notifications non lues
     */
    public List<Notification> getNotificationsNonLues() {
        if (utilisateurConnecte == null) {
            return List.of();
        }
        
        try {
            List<Notification> notifications = notificationService.getNotificationsNonLues(utilisateurConnecte);
            // TODO: view.afficherNotificationsNonLues(notifications);
            return notifications;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Compte le nombre de notifications non lues.
     * 
     * @return Le nombre de notifications non lues
     */
    public int compterNotificationsNonLues() {
        if (utilisateurConnecte == null) {
            return 0;
        }
        
        try {
            int count = notificationService.compterNotificationsNonLues(utilisateurConnecte);
            // TODO: view.afficherBadgeNotifications(count);
            return count;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors du comptage : " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Marque une notification comme lue.
     * Appelé quand l'utilisateur clique sur une notification.
     * 
     * @param notification La notification à marquer comme lue
     */
    public void marquerCommeLue(Notification notification) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (notification == null) {
            // TODO: view.showError("Veuillez sélectionner une notification");
            return;
        }
        
        // 2. Vérifier que la notification appartient à l'utilisateur
        if (notification.getDestinataire() == null || 
            notification.getDestinataire().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette notification ne vous appartient pas");
            return;
        }
        
        // 3. Déléguer au Service
        try {
            boolean success = notificationService.marquerCommeLue(notification);
            if (success) {
                // TODO: view.rafraichirNotifications();
                // TODO: view.rafraichirBadge();
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    
    /**
     * Marque toutes les notifications comme lues.
     * Appelé quand l'utilisateur clique sur "Tout marquer comme lu".
     */
    public void marquerToutesCommeLues() {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            int count = notificationService.marquerToutesCommeLues(utilisateurConnecte);
            // TODO: view.showSuccess(count + " notification(s) marquée(s) comme lue(s)");
            // TODO: view.rafraichirNotifications();
            // TODO: view.rafraichirBadge();
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    
    /**
     * Supprime une notification.
     * 
     * @param notification La notification à supprimer
     */
    public void supprimerNotification(Notification notification) {
        // 1. Validation
        if (utilisateurConnecte == null) {
            // TODO: view.showError("Vous devez être connecté");
            return;
        }
        if (notification == null) {
            // TODO: view.showError("Veuillez sélectionner une notification");
            return;
        }
        
        // 2. Vérifier que la notification appartient à l'utilisateur
        if (notification.getDestinataire() == null || 
            notification.getDestinataire().getId() != utilisateurConnecte.getId()) {
            // TODO: view.showError("Cette notification ne vous appartient pas");
            return;
        }
        
        // 3. Déléguer au Service
        try {
            boolean success = notificationService.supprimerNotification(notification);
            if (success) {
                // TODO: view.showSuccess("Notification supprimée");
                // TODO: view.rafraichirNotifications();
            } else {
                // TODO: view.showError("Échec de la suppression");
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la suppression : " + e.getMessage());
        }
    }
    
    /**
     * Envoie une notification (pour les admins).
     * 
     * @param message Le message
     * @param type Le type (RAPPEL, ALERTE, INFO)
     * @param priorite La priorité (HAUTE, MOYENNE, BASSE)
     * @param destinataire Le destinataire
     */
    public void envoyerNotification(String message, String type, String priorite, Personne destinataire) {
        // 1. Validation
        if (utilisateurConnecte == null || !(utilisateurConnecte instanceof Admin)) {
            // TODO: view.showError("Vous devez être connecté en tant qu'admin");
            return;
        }
        if (message == null || message.isEmpty()) {
            // TODO: view.showError("Le message est requis");
            return;
        }
        if (destinataire == null) {
            // TODO: view.showError("Le destinataire est requis");
            return;
        }
        
        // 2. Déléguer au Service
        try {
            Notification notification = notificationService.envoyerNotification(
                message, 
                type, 
                priorite, 
                destinataire
            );
            // TODO: view.showSuccess("Notification envoyée avec succès");
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de l'envoi : " + e.getMessage());
        }
    }
}

