package com.infinitpages.util.constants;

/**
 * Enum représentant les genres de documents.
 * Un document a UN SEUL genre.
 */
public enum Genre {
    /**
     * Article scientifique ou académique
     */
    ARTICLE("Article"),
    
    /**
     * Roman, fiction littéraire
     */
    NOVEL("Roman"),
    
    /**
     * Livre scientifique, technique
     */
    LIVRE_SCIENTIFIQUE("Livre scientifique"),
    
    /**
     * Manga, bande dessinée japonaise
     */
    MANGA("Manga"),
    
    /**
     * Bande dessinée (non japonaise)
     */
    BANDE_DESSINEE("Bande dessinée"),
    
    /**
     * Magazine, périodique
     */
    MAGAZINE("Magazine"),
    
    /**
     * Thèse, mémoire
     */
    THESE("Thèse"),
    
    /**
     * Manuel, guide pratique
     */
    MANUEL("Manuel"),
    
    /**
     * Biographie
     */
    BIOGRAPHIE("Biographie"),
    
    /**
     * Essai
     */
    ESSAI("Essai"),
    
    /**
     * Poésie
     */
    POESIE("Poésie"),
    
    /**
     * Théâtre
     */
    THEATRE("Théâtre"),
    
    /**
     * Autre genre
     */
    AUTRE("Autre");
    
    private final String libelle;
    
    Genre(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

