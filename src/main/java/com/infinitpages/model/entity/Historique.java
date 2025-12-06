package com.infinitpages.model.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe représentant l'historique des actions et les listes de lecture d'un utilisateur.
 * Combine deux concepts : historique des actions et listes de lecture personnelles.
 */
public class Historique {
    
    // Identifiant
    protected int id;
    
    // "HISTORIQUE" ou "LISTE_LECTURE"
    protected String type;
    
    // Titre (ex: "Mes lectures d'été", "Action: Emprunt")
    protected String titre;
    
    // Description
    protected String description;
    
    // Date de création
    protected LocalDateTime dateCreation;
    
    // Dernière modification
    protected LocalDateTime dateModification;
    
    // La liste est-elle partagée avec d'autres ?
    protected boolean estPartage;
    
    // Détails de l'action ou de la liste
    protected String details;
    
    // Relations : Référence à l'utilisateur propriétaire
    protected Utilisateur utilisateur;
    
    // Relations : Liste des documents (pour les listes de lecture)
    protected List<Document> documents;
    
    // Constructeurs
    public Historique() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        this.estPartage = false;
    }
    
    public Historique(String type, String titre, Utilisateur utilisateur) {
        this();
        this.type = type;
        this.titre = titre;
        this.utilisateur = utilisateur;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    public boolean isEstPartage() {
        return estPartage;
    }
    
    public void setEstPartage(boolean estPartage) {
        this.estPartage = estPartage;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}

