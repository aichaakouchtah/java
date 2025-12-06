package com.infinitpages.model.entity;

import java.time.LocalDate;

/**
 * Classe représentant un paiement effectué par un utilisateur.
 * Peut être lié à un emprunt (pénalité) ou à d'autres motifs.
 */
public class Paiement {
    
    // Identifiant unique du paiement
    protected int id;
    
    // Montant payé
    protected double montant;
    
    // Date du paiement
    protected LocalDate datePaiement;
    
    // Méthode de paiement ("ESPECES", "CARTE", "CHEQUE", "VIREMENT")
    protected String methodePaiement;
    
    // Statut du paiement ("EN_ATTENTE", "VALIDE", "ANNULE", "REFUSE")
    protected String statut;
    
    // Référence/numéro de transaction
    protected String reference;
    
    // Motif du paiement (ex: "Pénalité retard", "Frais d'emprunt")
    protected String motif;
    
    // Relations : Référence à l'emprunt qui a généré ce paiement (peut être null)
    protected Emprunt emprunt;
    
    // Relations : Référence à l'utilisateur qui a effectué le paiement
    protected Utilisateur utilisateur;
    
    // Constructeurs
    public Paiement() {
        this.datePaiement = LocalDate.now();
        this.statut = "EN_ATTENTE";
        this.montant = 0.0;
    }
    
    public Paiement(double montant, String methodePaiement, String motif, Utilisateur utilisateur) {
        this();
        this.montant = montant;
        this.methodePaiement = methodePaiement;
        this.motif = motif;
        this.utilisateur = utilisateur;
    }
    
    public Paiement(double montant, String methodePaiement, String motif, Utilisateur utilisateur, Emprunt emprunt) {
        this(montant, methodePaiement, motif, utilisateur);
        this.emprunt = emprunt;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getMontant() {
        return montant;
    }
    
    public void setMontant(double montant) {
        this.montant = montant;
    }
    
    public LocalDate getDatePaiement() {
        return datePaiement;
    }
    
    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }
    
    public String getMethodePaiement() {
        return methodePaiement;
    }
    
    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public Emprunt getEmprunt() {
        return emprunt;
    }
    
    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}

