package com.infinitpages.model.entity;

import java.time.LocalDateTime;

/**
 * Classe abstraite représentant une personne dans le système de bibliothèque.
 * Classe mère pour tous les types d'utilisateurs (Utilisateur, Admin, SuperAdmin).
 */
public abstract class Personne {
    
    // Identifiant unique de la personne
    protected int id;
    
    // Nom complet
    protected String nom;
    
    // Adresse email (pour la connexion)
    protected String email;
    
    // Mot de passe (crypté)
    protected String motDePasse;
    
    // Date de création du compte
    protected LocalDateTime dateInscription;
    
    // Le compte est-il actif ou désactivé ?
    protected boolean estActif;
    
    // Constructeurs
    public Personne() {
        this.dateInscription = LocalDateTime.now();
        this.estActif = true;
    }
    
    public Personne(String nom, String email, String motDePasse) {
        this();
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    public boolean isEstActif() {
        return estActif;
    }
    
    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }
}

