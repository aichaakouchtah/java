package com.infinitpages.util.constants;

/**
 * Enum représentant les types d'utilisateurs disponibles lors de la création de compte.
 * Définit les limites d'emprunt, durées et jours gratuits pour chaque type.
 */
public enum TypeUtilisateur {
    /**
     * Personne normale - Limité à 3 livres, 15 jours max, pas de jours gratuits
     */
    PERSONNE_NORMALE("Personne normale", 3, 15, 0),
    
    /**
     * Étudiant - Limité à 5 livres, 20 jours max, 5 jours gratuits (si prêt > 10 jours)
     */
    ETUDIANT("Étudiant", 5, 20, 5),
    
    /**
     * Enseignant - Limité à 10 livres, 30 jours max, 3 jours gratuits (si prêt > 10 jours)
     */
    ENSEIGNANT("Enseignant", 10, 30, 3);
    
    private final String libelle;
    private final int limiteEmprunts;
    private final int dureeEmpruntJours;
    private final int joursGratuits;
    
    TypeUtilisateur(String libelle, int limiteEmprunts, int dureeEmpruntJours, int joursGratuits) {
        this.libelle = libelle;
        this.limiteEmprunts = limiteEmprunts;
        this.dureeEmpruntJours = dureeEmpruntJours;
        this.joursGratuits = joursGratuits;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public int getLimiteEmprunts() {
        return limiteEmprunts;
    }
    
    public int getDureeEmpruntJours() {
        return dureeEmpruntJours;
    }
    
    public int getJoursGratuits() {
        return joursGratuits;
    }
    
    /**
     * Retourne le nombre de jours gratuits si le prêt dépasse 10 jours, sinon 0
     */
    public int getJoursGratuitsApplicables(int dureeEmprunt) {
        if (dureeEmprunt > 10) {
            return joursGratuits;
        }
        return 0;
    }
}

