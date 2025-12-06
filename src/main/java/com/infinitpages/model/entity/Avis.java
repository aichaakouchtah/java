package com.infinitpages.model.entity;

import java.time.LocalDateTime;

/**
 * Classe représentant un avis (note + commentaire) donné par un utilisateur sur un document.
 */
public class Avis {
    
    // Identifiant de l'avis
    protected int id;
    
    // Texte du commentaire
    protected String contenu;
    
    // Note de 1 à 5 étoiles
    protected int note;
    
    // Date de publication
    protected LocalDateTime dateAvis;
    
    // A-t-il été vérifié/approuvé par un admin ?
    protected boolean estModere;
    
    // Relations : Référence à l'utilisateur qui a écrit l'avis
    protected Utilisateur utilisateur;
    
    // Relations : Référence au document concerné
    protected Document document;
    
    // Constructeurs
    public Avis() {
        this.dateAvis = LocalDateTime.now();
        this.estModere = false;
    }
    
    public Avis(String contenu, int note, Utilisateur utilisateur, Document document) {
        this();
        this.contenu = contenu;
        this.note = note;
        this.utilisateur = utilisateur;
        this.document = document;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    
    public int getNote() {
        return note;
    }
    
    public void setNote(int note) {
        this.note = note;
    }
    
    public LocalDateTime getDateAvis() {
        return dateAvis;
    }
    
    public void setDateAvis(LocalDateTime dateAvis) {
        this.dateAvis = dateAvis;
    }
    
    public boolean isEstModere() {
        return estModere;
    }
    
    public void setEstModere(boolean estModere) {
        this.estModere = estModere;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public Document getDocument() {
        return document;
    }
    
    public void setDocument(Document document) {
        this.document = document;
    }
}

