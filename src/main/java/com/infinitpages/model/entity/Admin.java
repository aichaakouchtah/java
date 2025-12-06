package com.infinitpages.model.entity;

import com.infinitpages.util.constants.TypeAdmin;

/**
 * Classe représentant un administrateur (Bibliothécaire) du système.
 * Hérite de Personne et gère la bibliothèque au quotidien.
 * Le type d'admin détermine quels types de documents il peut gérer.
 * Les permissions seront gérées plus tard.
 */
public class Admin extends Personne {
    
    // Type d'administration (REEL_ONLY, NUMERIQUE_ONLY, ou BOTH)
    protected TypeAdmin typeAdmin;
    
    // Département de la bibliothèque dont il est responsable
    protected String departement;
    
    // Constructeurs
    public Admin() {
        super();
    }
    
    public Admin(String nom, String email, String motDePasse, String departement, TypeAdmin typeAdmin) {
        super(nom, email, motDePasse);
        this.departement = departement;
        this.typeAdmin = typeAdmin;
    }
    
    // Getters et Setters
    public TypeAdmin getTypeAdmin() {
        return typeAdmin;
    }
    
    public void setTypeAdmin(TypeAdmin typeAdmin) {
        this.typeAdmin = typeAdmin;
    }
    
    public String getDepartement() {
        return departement;
    }
    
    public void setDepartement(String departement) {
        this.departement = departement;
    }
    
    /**
     * Vérifie si l'admin peut gérer les documents réels
     */
    public boolean peutGererReels() {
        return typeAdmin != null && typeAdmin.peutGererReels();
    }
    
    /**
     * Vérifie si l'admin peut gérer les documents numériques
     */
    public boolean peutGererNumeriques() {
        return typeAdmin != null && typeAdmin.peutGererNumeriques();
    }
}

