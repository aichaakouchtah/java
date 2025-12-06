package com.infinitpages.model.entity;

import com.infinitpages.util.constants.TypeUtilisateur;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe représentant un emprunt de document par un utilisateur.
 * Gère les calculs de prix, jours gratuits et pénalités selon les règles.
 */
public class Emprunt {
    
    // Identifiant de l'emprunt
    protected int id;
    
    // Quand le livre a été emprunté
    protected LocalDate dateEmprunt;
    
    // Quand il doit être retourné
    protected LocalDate dateRetour;
    
    // Quand il a vraiment été retourné
    protected LocalDate dateRetourEffective;
    
    // État de l'emprunt ("EN_COURS", "RETOURNE", "EN_RETARD")
    protected String etat;
    
    // Nombre de jours maximum (ex: 14 jours)
    protected int dureeMax;
    
    // Montant de la pénalité si retard
    protected double penalite;
    
    // Statut du paiement ("PAYE", "NON_PAYE")
    protected String statut;
    
    // Montant payé en cash
    protected double montantPaye;
    
    // Date du paiement de la pénalité
    protected LocalDate datePaiement;
    
    // Relations : Référence à l'utilisateur qui a emprunté
    protected Utilisateur utilisateur;
    
    // Relations : Référence au document emprunté
    protected Document document;
    
    // Constructeurs
    public Emprunt() {
        this.etat = "EN_COURS";
        this.statut = "NON_PAYE";
        this.penalite = 0.0;
        this.montantPaye = 0.0;
        this.dureeMax = 14; // Par défaut 14 jours
    }
    
    public Emprunt(Utilisateur utilisateur, Document document, LocalDate dateEmprunt, int dureeMax) {
        this();
        this.utilisateur = utilisateur;
        this.document = document;
        this.dateEmprunt = dateEmprunt;
        this.dureeMax = dureeMax;
        this.dateRetour = dateEmprunt.plusDays(dureeMax);
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }
    
    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }
    
    public LocalDate getDateRetour() {
        return dateRetour;
    }
    
    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }
    
    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }
    
    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }
    
    public String getEtat() {
        return etat;
    }
    
    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    public int getDureeMax() {
        return dureeMax;
    }
    
    public void setDureeMax(int dureeMax) {
        this.dureeMax = dureeMax;
    }
    
    public double getPenalite() {
        return penalite;
    }
    
    public void setPenalite(double penalite) {
        this.penalite = penalite;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public double getMontantPaye() {
        return montantPaye;
    }
    
    public void setMontantPaye(double montantPaye) {
        this.montantPaye = montantPaye;
    }
    
    public LocalDate getDatePaiement() {
        return datePaiement;
    }
    
    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
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
    
    // ========== MÉTHODES DE CALCUL ==========
    
    /**
     * Calcule le nombre de jours de retard si le document est retourné après la date de retour
     * @return Nombre de jours de retard (0 si pas de retard)
     */
    public long calculerJoursRetard() {
        if (dateRetourEffective == null) {
            // Si pas encore retourné, calculer depuis aujourd'hui
            LocalDate aujourdhui = LocalDate.now();
            if (aujourdhui.isAfter(dateRetour)) {
                return ChronoUnit.DAYS.between(dateRetour, aujourdhui);
            }
            return 0;
        }
        // Si déjà retourné, calculer depuis la date de retour effective
        if (dateRetourEffective.isAfter(dateRetour)) {
            return ChronoUnit.DAYS.between(dateRetour, dateRetourEffective);
        }
        return 0;
    }
    
    /**
     * Retourne le nombre de jours gratuits applicables selon le type d'utilisateur
     * Les jours gratuits s'appliquent uniquement si le prêt dépasse 10 jours
     * @return Nombre de jours gratuits (0 si prêt <= 10 jours ou personne normale)
     */
    public int getJoursGratuitsApplicables() {
        if (utilisateur == null || utilisateur.getTypeUtilisateur() == null) {
            return 0;
        }
        TypeUtilisateur type = utilisateur.getTypeUtilisateur();
        return type.getJoursGratuitsApplicables(dureeMax);
    }
    
    /**
     * Calcule le nombre de jours facturables (durée - jours gratuits)
     * @return Nombre de jours pour lesquels l'utilisateur doit payer
     */
    public int calculerJoursFacturables() {
        int joursGratuits = getJoursGratuitsApplicables();
        int joursFacturables = dureeMax - joursGratuits;
        return Math.max(0, joursFacturables); // Ne peut pas être négatif
    }
    
    /**
     * Calcule le prix total de l'emprunt (jours facturables × prix par jour)
     * @return Prix total à payer pour l'emprunt
     */
    public double calculerPrixTotal() {
        if (document == null) {
            return 0.0;
        }
        int joursFacturables = calculerJoursFacturables();
        return joursFacturables * document.getPrixParJour();
    }
    
    /**
     * Calcule la pénalité en cas de retard
     * Pénalité = (jours de retard × prix par jour × 2)
     * @return Montant de la pénalité
     */
    public double calculerPenalite() {
        long joursRetard = calculerJoursRetard();
        if (joursRetard <= 0 || document == null) {
            return 0.0;
        }
        // Pénalité = double du prix par jour pour chaque jour de retard
        double prixParJour = document.getPrixParJour();
        return joursRetard * prixParJour * 2.0;
    }
    
    /**
     * Calcule le montant total à payer (prix emprunt + pénalité si retard)
     * @return Montant total à payer
     */
    public double calculerMontantTotal() {
        double prixTotal = calculerPrixTotal();
        double penalite = calculerPenalite();
        return prixTotal + penalite;
    }
    
    /**
     * Vérifie si l'emprunt est en retard
     * @return true si en retard, false sinon
     */
    public boolean estEnRetard() {
        if (dateRetourEffective != null) {
            // Si déjà retourné, vérifier si retourné en retard
            return dateRetourEffective.isAfter(dateRetour);
        }
        // Si pas encore retourné, vérifier si la date de retour est passée
        return LocalDate.now().isAfter(dateRetour);
    }
    
    /**
     * Calcule le nombre de jours empruntés effectivement
     * @return Nombre de jours entre dateEmprunt et dateRetourEffective (ou aujourd'hui si pas encore retourné)
     */
    public long calculerJoursEmpruntes() {
        if (dateEmprunt == null) {
            return 0;
        }
        LocalDate dateFin = (dateRetourEffective != null) ? dateRetourEffective : LocalDate.now();
        return ChronoUnit.DAYS.between(dateEmprunt, dateFin);
    }
}

