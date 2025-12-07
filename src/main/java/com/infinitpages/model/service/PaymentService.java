package com.infinitpages.model.service;

import com.infinitpages.model.entity.Paiement;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.dao.PaiementDAO;
import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.impl.PaiementDAOImpl;
import com.infinitpages.model.dao.impl.UtilisateurDAOImpl;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des paiements.
 * Orchestre la logique métier pour les paiements.
 * 
 * Responsabilités :
 * - Traiter les paiements (pénalités, frais d'emprunt)
 * - Valider les paiements
 * - Mettre à jour le solde utilisateur
 * - Gérer l'historique des paiements
 * - Générer des références de paiement
 */
public class PaymentService {
    
    private PaiementDAO paiementDAO;
    private UtilisateurDAO utilisateurDAO;
    private EmpruntDAO empruntDAO;
    
    /**
     * Constructeur par défaut.
     */
    public PaymentService() {
        this.paiementDAO = new PaiementDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param paiementDAO Le DAO Paiement à utiliser
     * @param utilisateurDAO Le DAO Utilisateur à utiliser
     * @param empruntDAO Le DAO Emprunt à utiliser
     */
    public PaymentService(PaiementDAO paiementDAO, UtilisateurDAO utilisateurDAO, EmpruntDAO empruntDAO) {
        this.paiementDAO = paiementDAO;
        this.utilisateurDAO = utilisateurDAO;
        this.empruntDAO = empruntDAO;
    }
    
    /**
     * Traite un paiement pour une pénalité d'emprunt.
     * 
     * @param utilisateur L'utilisateur qui paie
     * @param emprunt L'emprunt avec pénalité
     * @param methodePaiement La méthode de paiement
     * @return Le paiement créé
     * @throws IllegalArgumentException Si les paramètres sont invalides
     * @throws IllegalStateException Si l'emprunt n'a pas de pénalité
     */
    public Paiement payerPenalite(Utilisateur utilisateur, Emprunt emprunt, String methodePaiement) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt ne peut pas être null");
        }
        if (methodePaiement == null || methodePaiement.isEmpty()) {
            throw new IllegalArgumentException("Méthode de paiement requise");
        }
        
        // Calculer la pénalité
        double penalite = emprunt.calculerPenalite();
        if (penalite <= 0) {
            throw new IllegalStateException("Cet emprunt n'a pas de pénalité à payer");
        }
        
        // Créer le paiement
        Paiement paiement = new Paiement(
            penalite,
            methodePaiement,
            "Pénalité retard - Emprunt #" + emprunt.getId(),
            utilisateur,
            emprunt
        );
        
        // Générer une référence unique
        String reference = genererReference(utilisateur.getId(), emprunt.getId());
        paiement.setReference(reference);
        
        try {
            // Sauvegarder le paiement
            paiement = paiementDAO.save(paiement);
            
            // Mettre à jour le solde de l'utilisateur
            mettreAJourSoldeUtilisateur(utilisateur, -penalite);
            
            return paiement;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement du paiement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Traite un paiement pour un montant général.
     * 
     * @param utilisateur L'utilisateur qui paie
     * @param montant Le montant à payer
     * @param methodePaiement La méthode de paiement
     * @param motif Le motif du paiement
     * @return Le paiement créé
     */
    public Paiement effectuerPaiement(Utilisateur utilisateur, double montant, 
                                      String methodePaiement, String motif) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (methodePaiement == null || methodePaiement.isEmpty()) {
            throw new IllegalArgumentException("Méthode de paiement requise");
        }
        
        // Créer le paiement
        Paiement paiement = new Paiement(montant, methodePaiement, motif, utilisateur);
        
        // Générer une référence unique
        String reference = genererReference(utilisateur.getId(), null);
        paiement.setReference(reference);
        
        try {
            // Sauvegarder le paiement
            paiement = paiementDAO.save(paiement);
            
            // Mettre à jour le solde de l'utilisateur
            mettreAJourSoldeUtilisateur(utilisateur, -montant);
            
            return paiement;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement du paiement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide un paiement (change le statut à VALIDE).
     * 
     * @param paiement Le paiement à valider
     * @return true si la validation a réussi
     */
    public boolean validerPaiement(Paiement paiement) {
        if (paiement == null) {
            throw new IllegalArgumentException("Paiement ne peut pas être null");
        }
        
        if (!"EN_ATTENTE".equals(paiement.getStatut())) {
            throw new IllegalStateException("Seuls les paiements en attente peuvent être validés");
        }
        
        paiement.setStatut("VALIDE");
        
        try {
            return paiementDAO.update(paiement);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la validation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Annule un paiement.
     * 
     * @param paiement Le paiement à annuler
     * @return true si l'annulation a réussi
     */
    public boolean annulerPaiement(Paiement paiement) {
        if (paiement == null) {
            throw new IllegalArgumentException("Paiement ne peut pas être null");
        }
        
        if ("VALIDE".equals(paiement.getStatut())) {
            // Si le paiement était validé, rembourser l'utilisateur
            Utilisateur utilisateur = paiement.getUtilisateur();
            if (utilisateur != null) {
                mettreAJourSoldeUtilisateur(utilisateur, paiement.getMontant());
            }
        }
        
        paiement.setStatut("ANNULE");
        
        try {
            return paiementDAO.update(paiement);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'annulation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère tous les paiements d'un utilisateur.
     * 
     * @param utilisateur L'utilisateur
     * @return Liste des paiements
     */
    public List<Paiement> getPaiementsUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        
        try {
            return paiementDAO.findByUtilisateur(utilisateur.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les paiements en attente d'un utilisateur.
     * 
     * @param utilisateur L'utilisateur
     * @return Liste des paiements en attente
     */
    public List<Paiement> getPaiementsEnAttente(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null");
        }
        
        try {
            List<Paiement> tousPaiements = paiementDAO.findByUtilisateur(utilisateur.getId());
            return tousPaiements.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut()))
                .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcule le total des paiements validés d'un utilisateur.
     * 
     * @param utilisateur L'utilisateur
     * @return Le montant total payé
     */
    public double calculerTotalPaye(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return 0.0;
        }
        
        try {
            return paiementDAO.getTotalPaiements(utilisateur.getId());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère un paiement par sa référence.
     * 
     * @param reference La référence du paiement
     * @return Le paiement trouvé
     */
    public Optional<Paiement> getPaiementParReference(String reference) {
        if (reference == null || reference.isEmpty()) {
            return Optional.empty();
        }
        
        try {
            return paiementDAO.findByReference(reference);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour le solde de l'utilisateur après un paiement.
     * 
     * @param utilisateur L'utilisateur
     * @param montant Le montant (négatif pour déduire, positif pour ajouter)
     */
    private void mettreAJourSoldeUtilisateur(Utilisateur utilisateur, double montant) {
        try {
            double nouveauSolde = Math.max(0, utilisateur.getSoldeAPayer() + montant);
            utilisateurDAO.updateSoldeAPayer(utilisateur.getId(), nouveauSolde);
            utilisateur.setSoldeAPayer(nouveauSolde);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du solde: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère une référence unique pour un paiement.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @param idEmprunt L'identifiant de l'emprunt (peut être null)
     * @return La référence générée
     */
    private String genererReference(int idUtilisateur, Integer idEmprunt) {
        long timestamp = System.currentTimeMillis();
        String base = "PAY-" + idUtilisateur + "-" + timestamp;
        if (idEmprunt != null) {
            base += "-EMP" + idEmprunt;
        }
        return base;
    }
}

