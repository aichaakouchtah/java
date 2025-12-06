package com.infinitpages.model.entity;

import com.infinitpages.util.constants.TypeUtilisateur;

/**
 * Classe représentant un utilisateur (Étudiant/Enseignant/Personne normale) du système.
 * Hérite de Personne et représente les utilisateurs qui empruntent et consultent des documents.
 */
public class Utilisateur extends Personne {
    
    // Type d'utilisateur (ETUDIANT, ENSEIGNANT, ou PERSONNE_NORMALE)
    protected TypeUtilisateur typeUtilisateur;
    
    // Nombre maximum de documents qu'il peut emprunter
    // (défini automatiquement selon le TypeUtilisateur)
    protected int limiteEmprunts;
    
    // Montant des pénalités de retard qu'il doit payer
    protected double soldeAPayer;
    
    // Constructeurs
    public Utilisateur() {
        super();
        this.soldeAPayer = 0.0;
    }
    
    public Utilisateur(String nom, String email, String motDePasse, TypeUtilisateur typeUtilisateur) {
        super(nom, email, motDePasse);
        this.typeUtilisateur = typeUtilisateur;
        this.soldeAPayer = 0.0;
        // Définir automatiquement la limite et la durée selon le type
        if (typeUtilisateur != null) {
            this.limiteEmprunts = typeUtilisateur.getLimiteEmprunts();
        }
    }
    
    // Getters et Setters
    public TypeUtilisateur getTypeUtilisateur() {
        return typeUtilisateur;
    }
    
    public void setTypeUtilisateur(TypeUtilisateur typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
        // Mettre à jour automatiquement la limite d'emprunts
        if (typeUtilisateur != null) {
            this.limiteEmprunts = typeUtilisateur.getLimiteEmprunts();
        }
    }
    
    public int getLimiteEmprunts() {
        return limiteEmprunts;
    }
    
    public void setLimiteEmprunts(int limiteEmprunts) {
        this.limiteEmprunts = limiteEmprunts;
    }
    
    public double getSoldeAPayer() {
        return soldeAPayer;
    }
    
    public void setSoldeAPayer(double soldeAPayer) {
        this.soldeAPayer = soldeAPayer;
    }
    
    /**
     * Retourne la durée d'emprunt en jours selon le type d'utilisateur
     */
    public int getDureeEmpruntJours() {
        return typeUtilisateur != null ? typeUtilisateur.getDureeEmpruntJours() : 14;
    }
}

