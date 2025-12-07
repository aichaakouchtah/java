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
    
    /**
     * Vérifie si l'admin peut gérer les deux types de documents (réels et numériques)
     * 
     * @return true si l'admin peut gérer les deux types
     */
    public boolean peutGererLesDeux() {
        return typeAdmin != null && typeAdmin == TypeAdmin.BOTH;
    }
    
    /**
     * Vérifie si l'admin a des permissions valides
     * 
     * @return true si l'admin a un typeAdmin défini
     */
    public boolean aDesPermissions() {
        return typeAdmin != null;
    }
    
    /**
     * Vérifie si l'admin est valide (actif et avec permissions)
     * 
     * @return true si l'admin est actif et a des permissions
     */
    public boolean estValide() {
        return isEstActif() && aDesPermissions();
    }
    
    /**
     * Vérifie si l'email de l'admin est valide (format basique)
     * 
     * @return true si l'email contient @ et .
     */
    public boolean emailEstValide() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
    
    /**
     * Vérifie si le département est défini
     * 
     * @return true si le département n'est pas null et pas vide
     */
    public boolean aUnDepartement() {
        return departement != null && !departement.trim().isEmpty();
    }
    
    /**
     * Retourne une description des permissions de l'admin
     * 
     * @return Description des permissions
     */
    public String getDescriptionPermissions() {
        if (typeAdmin == null) {
            return "Aucune permission définie";
        }
        return typeAdmin.getDescription();
    }
    
    /**
     * Vérifie si l'admin peut gérer un document spécifique
     * 
     * @param estDocumentReel true si c'est un document réel, false si numérique
     * @return true si l'admin peut gérer ce type de document
     */
    public boolean peutGererDocument(boolean estDocumentReel) {
        if (!estValide()) {
            return false;
        }
        if (estDocumentReel) {
            return peutGererReels();
        } else {
            return peutGererNumeriques();
        }
    }
    
    /**
     * Retourne un résumé de l'admin (pour affichage)
     * 
     * @return Résumé de l'admin
     */
    public String getResume() {
        return String.format("Admin: %s (%s) - Département: %s - Permissions: %s",
            nom != null ? nom : "N/A",
            email != null ? email : "N/A",
            departement != null ? departement : "N/A",
            getDescriptionPermissions()
        );
    }
}

