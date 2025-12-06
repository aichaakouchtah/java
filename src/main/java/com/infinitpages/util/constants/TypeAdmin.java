package com.infinitpages.util.constants;

/**
 * Enum représentant le type d'administration d'un bibliothécaire.
 * Détermine quels types de documents il peut gérer.
 * Les permissions seront gérées plus tard.
 */
public enum TypeAdmin {
    /**
     * Admin responsable uniquement des documents réels (physiques)
     */
    REEL_ONLY("Documents réels uniquement"),
    
    /**
     * Admin responsable uniquement des documents numériques
     */
    NUMERIQUE_ONLY("Documents numériques uniquement"),
    
    /**
     * Admin responsable des deux types de documents
     */
    BOTH("Documents réels et numériques");
    
    private final String description;
    
    TypeAdmin(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si ce type d'admin peut gérer les documents réels
     */
    public boolean peutGererReels() {
        return this == REEL_ONLY || this == BOTH;
    }
    
    /**
     * Vérifie si ce type d'admin peut gérer les documents numériques
     */
    public boolean peutGererNumeriques() {
        return this == NUMERIQUE_ONLY || this == BOTH;
    }
}

