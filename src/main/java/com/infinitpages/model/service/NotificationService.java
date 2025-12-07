package com.infinitpages.model.service;

import com.infinitpages.model.entity.Notification;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.dao.NotificationDAO;
import com.infinitpages.model.dao.impl.NotificationDAOImpl;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour la gestion des notifications.
 * Orchestre la logique métier pour les notifications.
 * 
 * Responsabilités :
 * - Envoyer des notifications aux utilisateurs
 * - Gérer les notifications non lues
 * - Marquer les notifications comme lues
 * - Envoyer des rappels automatiques
 */
public class NotificationService {
    
    private NotificationDAO notificationDAO;
    
    /**
     * Constructeur par défaut.
     */
    public NotificationService() {
        this.notificationDAO = new NotificationDAOImpl();
    }
    
    /**
     * Constructeur avec injection du DAO (pour les tests).
     * 
     * @param notificationDAO Le DAO Notification à utiliser
     */
    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }
    
    /**
     * Envoie une notification à une personne.
     * 
     * @param message Le message de la notification
     * @param type Le type (RAPPEL, ALERTE, INFO)
     * @param priorite La priorité (HAUTE, MOYENNE, BASSE)
     * @param destinataire Le destinataire
     * @return La notification créée
     */
    public Notification envoyerNotification(String message, String type, String priorite, Personne destinataire) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Le message est requis");
        }
        if (destinataire == null) {
            throw new IllegalArgumentException("Le destinataire est requis");
        }
        
        Notification notification = new Notification(message, type, destinataire);
        if (priorite != null) {
            notification.setPriorite(priorite);
        }
        
        try {
            return notificationDAO.save(notification);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de la notification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Envoie un rappel de retour pour un emprunt.
     * 
     * @param emprunt L'emprunt concerné
     * @return La notification créée
     */
    public Notification envoyerRappelRetour(Emprunt emprunt) {
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt ne peut pas être null");
        }
        
        Utilisateur utilisateur = emprunt.getUtilisateur();
        if (utilisateur == null) {
            throw new IllegalStateException("L'emprunt n'a pas d'utilisateur associé");
        }
        
        Document document = emprunt.getDocument();
        String titreDocument = document != null ? document.getTitre() : "Document";
        
        // Calculer les jours restants
        LocalDate dateRetour = emprunt.getDateRetour();
        LocalDate aujourdhui = LocalDate.now();
        long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(aujourdhui, dateRetour);
        
        String message;
        String priorite;
        
        if (joursRestants < 0) {
            // En retard
            long joursRetard = Math.abs(joursRestants);
            message = String.format(
                "⚠️ RETOUR EN RETARD - Le document '%s' est en retard de %d jour(s). " +
                "Veuillez le retourner rapidement pour éviter des pénalités supplémentaires.",
                titreDocument, joursRetard
            );
            priorite = "HAUTE";
        } else if (joursRestants <= 3) {
            // Bientôt en retard
            message = String.format(
                "📚 RAPPEL - Le document '%s' doit être retourné dans %d jour(s).",
                titreDocument, joursRestants
            );
            priorite = "MOYENNE";
        } else {
            // Rappel normal
            message = String.format(
                "📖 RAPPEL - Le document '%s' doit être retourné le %s.",
                titreDocument, dateRetour.toString()
            );
            priorite = "BASSE";
        }
        
        return envoyerNotification(message, "RAPPEL", priorite, utilisateur);
    }
    
    /**
     * Envoie une notification pour un nouveau document disponible.
     * 
     * @param document Le nouveau document
     * @param utilisateurs Liste des utilisateurs à notifier
     */
    public void notifierNouveauDocument(Document document, List<Utilisateur> utilisateurs) {
        if (document == null) {
            throw new IllegalArgumentException("Document ne peut pas être null");
        }
        if (utilisateurs == null || utilisateurs.isEmpty()) {
            return;
        }
        
        String message = String.format(
            "📚 NOUVEAU DOCUMENT - '%s' par %s est maintenant disponible !",
            document.getTitre(),
            document.getAuteur()
        );
        
        for (Utilisateur utilisateur : utilisateurs) {
            envoyerNotification(message, "INFO", "BASSE", utilisateur);
        }
    }
    
    /**
     * Envoie une notification de pénalité.
     * 
     * @param utilisateur L'utilisateur concerné
     * @param emprunt L'emprunt avec pénalité
     * @param montantPenalite Le montant de la pénalité
     * @return La notification créée
     */
    public Notification notifierPenalite(Utilisateur utilisateur, Emprunt emprunt, double montantPenalite) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt ne peut pas être null");
        }
        
        Document document = emprunt.getDocument();
        String titreDocument = document != null ? document.getTitre() : "Document";
        
        String message = String.format(
            "💰 PÉNALITÉ - Une pénalité de %.2f € a été appliquée pour le retard du document '%s'. " +
            "Veuillez régler cette pénalité.",
            montantPenalite, titreDocument
        );
        
        return envoyerNotification(message, "ALERTE", "HAUTE", utilisateur);
    }
    
    /**
     * Récupère toutes les notifications d'une personne.
     * 
     * @param personne La personne
     * @return Liste des notifications
     */
    public List<Notification> getNotifications(Personne personne) {
        if (personne == null) {
            throw new IllegalArgumentException("Personne ne peut pas être null");
        }
        
        try {
            return notificationDAO.findByDestinataire(personne.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les notifications non lues d'une personne.
     * 
     * @param personne La personne
     * @return Liste des notifications non lues
     */
    public List<Notification> getNotificationsNonLues(Personne personne) {
        if (personne == null) {
            throw new IllegalArgumentException("Personne ne peut pas être null");
        }
        
        try {
            return notificationDAO.findNonLuesByDestinataire(personne.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de notifications non lues d'une personne.
     * 
     * @param personne La personne
     * @return Le nombre de notifications non lues
     */
    public int compterNotificationsNonLues(Personne personne) {
        if (personne == null) {
            return 0;
        }
        
        try {
            return notificationDAO.countNonLues(personne.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du comptage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Marque une notification comme lue.
     * 
     * @param notification La notification à marquer
     * @return true si la mise à jour a réussi
     */
    public boolean marquerCommeLue(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification ne peut pas être null");
        }
        
        notification.setEstLue(true);
        notification.setDateLecture(LocalDateTime.now());
        
        try {
            return notificationDAO.update(notification);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }
    
    /**
     * Marque toutes les notifications d'une personne comme lues.
     * 
     * @param personne La personne
     * @return Le nombre de notifications marquées comme lues
     */
    public int marquerToutesCommeLues(Personne personne) {
        if (personne == null) {
            throw new IllegalArgumentException("Personne ne peut pas être null");
        }
        
        try {
            List<Notification> notificationsNonLues = getNotificationsNonLues(personne);
            int count = 0;
            
            for (Notification notification : notificationsNonLues) {
                if (marquerCommeLue(notification)) {
                    count++;
                }
            }
            
            return count;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime une notification.
     * 
     * @param notification La notification à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimerNotification(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification ne peut pas être null");
        }
        
        try {
            return notificationDAO.delete(notification.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }
}

