package com.infinitpages.model.entity;

import java.time.LocalDateTime;

/**
 * Classe représentant une notification envoyée aux utilisateurs.
 * Alertes pour rappels de retour, nouveaux documents, etc.
 */
public class Notification {
    
    // Identifiant
    protected int id;
    
    // Contenu du message
    protected String message;
    
    // Quand elle a été envoyée
    protected LocalDateTime dateEnvoi;
    
    // Quand l'utilisateur l'a lue
    protected LocalDateTime dateLecture;
    
    // A-t-elle été lue ?
    protected boolean estLue;
    
    // Type ("RAPPEL", "ALERTE", "INFO")
    protected String type;
    
    // Niveau de priorité ("HAUTE", "MOYENNE", "BASSE")
    protected String priorite;
    
    // Relations : Référence à la personne destinataire
    protected Personne destinataire;
    
    // Constructeurs
    public Notification() {
        this.dateEnvoi = LocalDateTime.now();
        this.estLue = false;
        this.priorite = "MOYENNE";
    }
    
    public Notification(String message, String type, Personne destinataire) {
        this();
        this.message = message;
        this.type = type;
        this.destinataire = destinataire;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }
    
    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    
    public LocalDateTime getDateLecture() {
        return dateLecture;
    }
    
    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }
    
    public boolean isEstLue() {
        return estLue;
    }
    
    public void setEstLue(boolean estLue) {
        this.estLue = estLue;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPriorite() {
        return priorite;
    }
    
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
    
    public Personne getDestinataire() {
        return destinataire;
    }
    
    public void setDestinataire(Personne destinataire) {
        this.destinataire = destinataire;
    }
}

