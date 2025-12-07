package com.infinitpages.model.service;

import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.DocumentReel;
import com.infinitpages.model.entity.DocumentNumerique;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Avis;
import com.infinitpages.model.entity.Rapport;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.dao.AdminDAO;
import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.dao.CategorieDAO;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.AvisDAO;
import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.dao.RapportDAO;
import com.infinitpages.model.dao.impl.AdminDAOImpl;
import com.infinitpages.model.dao.impl.DocumentDAOImpl;
import com.infinitpages.model.dao.impl.CategorieDAOImpl;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;
import com.infinitpages.model.dao.impl.AvisDAOImpl;
import com.infinitpages.model.dao.impl.UtilisateurDAOImpl;
import com.infinitpages.model.dao.impl.RapportDAOImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les opérations administratives.
 * Orchestre la logique métier pour les admins (bibliothécaires).
 * 
 * Responsabilités :
 * - Gestion des documents (ajout, modification, suppression)
 * - Gestion des catégories
 * - Génération de rapports
 * - Gestion des emprunts et retours
 * - Modération des avis
 * - Suivi des activités utilisateurs
 */
public class AdminService {
    
    private AdminDAO adminDAO;
    private DocumentDAO documentDAO;
    private CategorieDAO categorieDAO;
    private EmpruntDAO empruntDAO;
    private AvisDAO avisDAO;
    private UtilisateurDAO utilisateurDAO;
    private RapportDAO rapportDAO;
    
    /**
     * Constructeur par défaut.
     */
    public AdminService() {
        this.adminDAO = new AdminDAOImpl();
        this.documentDAO = new DocumentDAOImpl();
        this.categorieDAO = new CategorieDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl();
        this.avisDAO = new AvisDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
        this.rapportDAO = new RapportDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param adminDAO Le DAO Admin à utiliser
     * @param documentDAO Le DAO Document à utiliser
     * @param categorieDAO Le DAO Categorie à utiliser
     * @param empruntDAO Le DAO Emprunt à utiliser
     * @param avisDAO Le DAO Avis à utiliser
     * @param utilisateurDAO Le DAO Utilisateur à utiliser
     * @param rapportDAO Le DAO Rapport à utiliser
     */
    public AdminService(AdminDAO adminDAO, DocumentDAO documentDAO, CategorieDAO categorieDAO,
                       EmpruntDAO empruntDAO, AvisDAO avisDAO, UtilisateurDAO utilisateurDAO,
                       RapportDAO rapportDAO) {
        this.adminDAO = adminDAO;
        this.documentDAO = documentDAO;
        this.categorieDAO = categorieDAO;
        this.empruntDAO = empruntDAO;
        this.avisDAO = avisDAO;
        this.utilisateurDAO = utilisateurDAO;
        this.rapportDAO = rapportDAO;
    }
    
    /**
     * Vérifie si l'admin a les permissions pour gérer ce type de document.
     */
    private boolean verifierPermissions(Admin admin, Document document) {
        if (admin == null || document == null) {
            return false;
        }
        
        if (document instanceof DocumentReel) {
            return admin.peutGererReels();
        } else if (document instanceof DocumentNumerique) {
            return admin.peutGererNumeriques();
        }
        
        return false;
    }
    
    /**
     * Ajoute un nouveau document à la bibliothèque.
     * 
     * @param admin L'admin qui effectue l'action
     * @param document Le document à ajouter
     * @throws IllegalStateException Si l'admin n'a pas les permissions
     */
    public void ajouterDocument(Admin admin, Document document) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (document == null) {
            throw new IllegalArgumentException("Document ne peut pas être null");
        }
        
        // Vérifier que l'admin est valide
        if (!admin.estValide()) {
            throw new IllegalStateException("L'admin n'est pas valide (inactif ou sans permissions)");
        }
        
        // Vérifier les permissions
        if (!verifierPermissions(admin, document)) {
            throw new IllegalStateException(
                "L'admin n'a pas les permissions pour gérer ce type de document"
            );
        }
        
        // Sauvegarder en base de données
        try {
            documentDAO.save(document);
            
            // Incrémenter le nombre de documents dans la catégorie
            if (document.getCategorieEntity() != null && document.getCategorieEntity().getId() > 0) {
                categorieDAO.incrementerNombreDocuments(document.getCategorieEntity().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Modifie un document existant.
     * 
     * @param admin L'admin qui effectue l'action
     * @param document Le document modifié
     * @throws IllegalStateException Si l'admin n'a pas les permissions
     */
    public void modifierDocument(Admin admin, Document document) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (document == null) {
            throw new IllegalArgumentException("Document ne peut pas être null");
        }
        
        // Vérifier que l'admin est valide
        if (!admin.estValide()) {
            throw new IllegalStateException("L'admin n'est pas valide (inactif ou sans permissions)");
        }
        
        // Vérifier les permissions
        if (!verifierPermissions(admin, document)) {
            throw new IllegalStateException(
                "L'admin n'a pas les permissions pour gérer ce type de document"
            );
        }
        
        // Mettre à jour en base de données
        try {
            boolean success = documentDAO.update(document);
            if (!success) {
                throw new IllegalStateException("Échec de la mise à jour du document");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modification du document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime un document de la bibliothèque.
     * 
     * @param admin L'admin qui effectue l'action
     * @param documentId L'identifiant du document à supprimer
     * @throws IllegalStateException Si l'admin n'a pas les permissions
     */
    public void supprimerDocument(Admin admin, int documentId) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Récupérer le document depuis la base
        Optional<Document> docOpt = documentDAO.findById(documentId);
        if (docOpt.isEmpty()) {
            throw new IllegalArgumentException("Document introuvable");
        }
        
        Document document = docOpt.get();
        
        // Vérifier les permissions
        if (!verifierPermissions(admin, document)) {
            throw new IllegalStateException(
                "L'admin n'a pas les permissions pour gérer ce type de document"
            );
        }
        
        // Vérifier qu'il n'y a pas d'emprunts en cours
        List<Emprunt> empruntsActifs = empruntDAO.findByDocument(documentId);
        boolean hasActiveLoans = empruntsActifs.stream()
            .anyMatch(e -> "EN_COURS".equals(e.getEtat()) || e.getDateRetourEffective() == null);
        
        if (hasActiveLoans) {
            throw new IllegalStateException("Impossible de supprimer : document en cours d'emprunt");
        }
        
        // Supprimer le document
        try {
            boolean success = documentDAO.delete(documentId);
            if (!success) {
                throw new IllegalStateException("Échec de la suppression du document");
            }
            
            // Décrémenter le nombre de documents dans la catégorie
            if (document.getCategorieEntity() != null && document.getCategorieEntity().getId() > 0) {
                categorieDAO.decrementerNombreDocuments(document.getCategorieEntity().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gère les catégories (ajout, modification, suppression).
     * 
     * @param admin L'admin qui effectue l'action
     * @param categorie La catégorie à gérer
     */
    public void gererCategories(Admin admin, Categorie categorie) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (categorie == null) {
            throw new IllegalArgumentException("Categorie ne peut pas être null");
        }
        
        // Gérer les catégories (ajout ou modification)
        try {
            if (categorie.getId() > 0) {
                // Mise à jour
                boolean success = categorieDAO.update(categorie);
                if (!success) {
                    throw new IllegalStateException("Échec de la mise à jour de la catégorie");
                }
            } else {
                // Création
                categorieDAO.save(categorie);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la gestion de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un rapport sur les emprunts.
     * 
     * @param admin L'admin qui génère le rapport
     * @param periode La période couverte (ex: "Janvier 2025")
     * @return Le rapport généré
     */
    public Rapport genererRapportEmprunts(Admin admin, String periode) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Récupérer les données depuis la base
        try {
            // Parser la période pour obtenir les dates
            LocalDate dateDebut = parsePeriode(periode, true);
            LocalDate dateFin = parsePeriode(periode, false);
            
            List<Emprunt> emprunts = empruntDAO.findByPeriode(dateDebut, dateFin);
            int totalEmprunts = emprunts.size();
            long empruntsEnRetard = emprunts.stream()
                .filter(Emprunt::estEnRetard)
                .count();
            double totalPenalites = emprunts.stream()
                .mapToDouble(Emprunt::calculerPenalite)
                .sum();
            
            // Construire le contenu du rapport
            String contenu = String.format(
                "Rapport des Emprunts - %s\n\n" +
                "Total d'emprunts: %d\n" +
                "Emprunts en retard: %d\n" +
                "Total des pénalités: %.2f €\n",
                periode, totalEmprunts, empruntsEnRetard, totalPenalites
            );
            
            Rapport rapport = new Rapport(
                "Rapport des Emprunts",
                "EMPRUNTS",
                periode,
                admin
            );
            rapport.setContenu(contenu);
            
            // Sauvegarder le rapport
            rapportDAO.save(rapport);
            
            return rapport;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un rapport sur les consultations.
     * 
     * @param admin L'admin qui génère le rapport
     * @param periode La période couverte
     * @return Le rapport généré
     */
    public Rapport genererRapportConsultations(Admin admin, String periode) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Récupérer les données depuis la base
        try {
            List<Document> documents = documentDAO.findAll();
            int totalConsultations = documents.stream()
                .mapToInt(Document::getNombreConsultations)
                .sum();
            Document documentPlusConsulte = documents.stream()
                .max(Comparator.comparingInt(Document::getNombreConsultations))
                .orElse(null);
            
            String nomDocumentPlusConsulte = documentPlusConsulte != null 
                ? documentPlusConsulte.getTitre() 
                : "Aucun";
            
            String contenu = String.format(
                "Rapport des Consultations - %s\n\n" +
                "Total de consultations: %d\n" +
                "Document le plus consulté: %s\n" +
                "Nombre de consultations du document le plus consulté: %d\n",
                periode, totalConsultations, nomDocumentPlusConsulte,
                documentPlusConsulte != null ? documentPlusConsulte.getNombreConsultations() : 0
            );
            
            Rapport rapport = new Rapport(
                "Rapport des Consultations",
                "CONSULTATIONS",
                periode,
                admin
            );
            rapport.setContenu(contenu);
            
            // Sauvegarder le rapport
            rapportDAO.save(rapport);
            
            return rapport;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport: " + e.getMessage(), e);
        }
    }
    
    /**
     * Suit l'activité des utilisateurs.
     * 
     * @param admin L'admin qui consulte
     * @return Liste des utilisateurs avec leur activité
     */
    public List<Utilisateur> suivreActiviteUtilisateurs(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Récupérer les utilisateurs avec leur activité
        try {
            return utilisateurDAO.findAllActifs();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gère les pénalités (visualisation, validation de paiement).
     * 
     * @param admin L'admin qui gère
     * @return Liste des emprunts avec pénalités
     */
    public List<Emprunt> gererPenalites(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Récupérer les emprunts avec pénalités
        try {
            List<Emprunt> empruntsEnRetard = empruntDAO.findEnRetard();
            // Filtrer ceux qui ont une pénalité > 0
            return empruntsEnRetard.stream()
                .filter(e -> e.calculerPenalite() > 0)
                .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des pénalités: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide le retour d'un document emprunté.
     * 
     * @param admin L'admin qui valide
     * @param emprunt L'emprunt à valider
     */
    public void validerRetour(Admin admin, Emprunt emprunt) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt ne peut pas être null");
        }
        
        // Utiliser LoanService pour retourner le document
        try {
            LoanService loanService = new LoanService();
            loanService.retournerDocument(emprunt);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la validation du retour: " + e.getMessage(), e);
        }
    }
    
    /**
     * Modère les avis (approuve ou rejette).
     * 
     * @param admin L'admin qui modère
     * @param avis L'avis à modérer
     * @param approuver true pour approuver, false pour rejeter
     */
    public void modererAvis(Admin admin, Avis avis, boolean approuver) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (avis == null) {
            throw new IllegalArgumentException("Avis ne peut pas être null");
        }
        
        try {
            if (approuver) {
                avis.setEstModere(true);
                avisDAO.update(avis);
                
                // Mettre à jour la note globale du document
                if (avis.getDocument() != null && avis.getDocument().getId() > 0) {
                    double nouvelleNote = avisDAO.calculerNoteGlobale(avis.getDocument().getId());
                    documentDAO.updateNoteGlobale(avis.getDocument().getId(), nouvelleNote);
                }
            } else {
                // Supprimer l'avis
                avisDAO.delete(avis.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modération de l'avis: " + e.getMessage(), e);
        }
    
    /**
     * Parse une période (ex: "Janvier 2025") en dates.
     * 
     * @param periode La période à parser
     * @param debut true pour la date de début, false pour la date de fin
     * @return La date correspondante
     */
    private LocalDate parsePeriode(String periode, boolean debut) {
        try {
            // Format attendu: "Janvier 2025" ou "01/2025"
            String[] parts = periode.split(" ");
            if (parts.length == 2) {
                String moisStr = parts[0];
                int annee = Integer.parseInt(parts[1]);
                
                int mois = getMoisFromString(moisStr);
                
                if (debut) {
                    return LocalDate.of(annee, mois, 1);
                } else {
                    return LocalDate.of(annee, mois, LocalDate.of(annee, mois, 1).lengthOfMonth());
                }
            }
            
            // Format alternatif: "01/2025"
            if (periode.contains("/")) {
                String[] dateParts = periode.split("/");
                int mois = Integer.parseInt(dateParts[0]);
                int annee = Integer.parseInt(dateParts[1]);
                
                if (debut) {
                    return LocalDate.of(annee, mois, 1);
                } else {
                    return LocalDate.of(annee, mois, LocalDate.of(annee, mois, 1).lengthOfMonth());
                }
            }
            
            // Par défaut, utiliser le mois et l'année actuels
            LocalDate now = LocalDate.now();
            if (debut) {
                return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            } else {
                return LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth());
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser le mois actuel
            LocalDate now = LocalDate.now();
            if (debut) {
                return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            } else {
                return LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth());
            }
        }
    }
    
    /**
     * Convertit un nom de mois en numéro.
     */
    private int getMoisFromString(String moisStr) {
        String[] mois = {"janvier", "fevrier", "mars", "avril", "mai", "juin",
                        "juillet", "aout", "septembre", "octobre", "novembre", "decembre"};
        for (int i = 0; i < mois.length; i++) {
            if (mois[i].equalsIgnoreCase(moisStr.toLowerCase())) {
                return i + 1;
            }
        }
        return LocalDate.now().getMonthValue();
    }
}
}

