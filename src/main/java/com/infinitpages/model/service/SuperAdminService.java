package com.infinitpages.model.service;

import com.infinitpages.model.entity.SuperAdmin;
import com.infinitpages.model.entity.Admin;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.entity.Rapport;
import com.infinitpages.model.dao.AdminDAO;
import com.infinitpages.model.dao.SuperAdminDAO;
import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.dao.RapportDAO;
import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.PaiementDAO;
import com.infinitpages.model.dao.impl.AdminDAOImpl;
import com.infinitpages.model.dao.impl.SuperAdminDAOImpl;
import com.infinitpages.model.dao.impl.UtilisateurDAOImpl;
import com.infinitpages.model.dao.impl.RapportDAOImpl;
import com.infinitpages.model.dao.impl.DocumentDAOImpl;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;
import com.infinitpages.model.dao.impl.PaiementDAOImpl;
import com.infinitpages.util.constants.TypeAdmin;
import com.infinitpages.util.constants.TypeUtilisateur;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service métier pour les opérations de Super Administrateur.
 * Orchestre la logique métier pour les super-admins.
 * 
 * Responsabilités :
 * - Gestion des admins (création, modification, suppression)
 * - Attribution de permissions aux admins
 * - Configuration système globale
 * - Génération de rapports globaux
 * - Gestion des tarifs et durées d'emprunt
 */
public class SuperAdminService {
    
    private AdminDAO adminDAO;
    private SuperAdminDAO superAdminDAO;
    private UtilisateurDAO utilisateurDAO;
    private RapportDAO rapportDAO;
    private DocumentDAO documentDAO;
    private EmpruntDAO empruntDAO;
    private PaiementDAO paiementDAO;
    
    /**
     * Constructeur par défaut.
     */
    public SuperAdminService() {
        this.adminDAO = new AdminDAOImpl();
        this.superAdminDAO = new SuperAdminDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
        this.rapportDAO = new RapportDAOImpl();
        this.documentDAO = new DocumentDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl();
        this.paiementDAO = new PaiementDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param adminDAO Le DAO Admin à utiliser
     * @param superAdminDAO Le DAO SuperAdmin à utiliser
     */
    public SuperAdminService(AdminDAO adminDAO, SuperAdminDAO superAdminDAO) {
        this.adminDAO = adminDAO;
        this.superAdminDAO = superAdminDAO;
    }
    
    /**
     * Crée un nouvel administrateur.
     * 
     * @param superAdmin Le super-admin qui effectue l'action
     * @param admin L'admin à créer
     * @throws IllegalStateException Si l'admin existe déjà
     */
    public void creerAdmin(SuperAdmin superAdmin, Admin admin) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Validation en utilisant les méthodes de l'entité
        if (!admin.emailEstValide()) {
            throw new IllegalArgumentException("L'email de l'admin est invalide");
        }
        
        if (!admin.aDesPermissions()) {
            throw new IllegalArgumentException("L'admin doit avoir des permissions définies");
        }
        
        try {
            // Vérifier si l'admin existe déjà
            Admin existingAdmin = adminDAO.findByEmail(admin.getEmail());
            if (existingAdmin != null) {
                throw new IllegalStateException("Un admin avec cet email existe déjà");
            }
            
            // TODO: Hasher le mot de passe avant sauvegarde
            // String hashedPassword = PasswordHasher.hash(admin.getMotDePasse());
            // admin.setMotDePasse(hashedPassword);
            
            // Sauvegarder en base
            adminDAO.save(admin);
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'admin: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime un administrateur.
     * 
     * @param superAdmin Le super-admin qui effectue l'action
     * @param admin L'admin à supprimer
     * @throws IllegalStateException Si on essaie de supprimer le super-admin lui-même
     */
    public void supprimerAdmin(SuperAdmin superAdmin, Admin admin) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        
        // Utiliser la méthode de l'entité pour vérifier
        if (!superAdmin.peutSupprimerAdmin(admin)) {
            if (admin.getId() == superAdmin.getId()) {
                throw new IllegalStateException("Un super-admin ne peut pas se supprimer lui-même");
            }
            if (admin instanceof SuperAdmin) {
                throw new IllegalStateException("Un super-admin ne peut pas supprimer un autre super-admin");
            }
            throw new IllegalStateException("Impossible de supprimer cet admin");
        }
        
        try {
            // TODO: Vérifier qu'il n'y a pas de rapports liés
            // List<Rapport> rapports = rapportDAO.findByAdmin(admin.getId());
            // if (!rapports.isEmpty()) {
            //     throw new IllegalStateException("Impossible de supprimer : l'admin a généré des rapports");
            // }
            
            // Désactiver plutôt que supprimer (soft delete)
            admin.setEstActif(false);
            adminDAO.update(admin);
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'admin: " + e.getMessage(), e);
        }
    }
    
    /**
     * Donne des permissions à un admin.
     * 
     * @param superAdmin Le super-admin qui effectue l'action
     * @param admin L'admin à qui donner les permissions
     * @param typeAdmin Le nouveau type d'admin (permissions)
     */
    public void givePermissionsToAdmin(SuperAdmin superAdmin, Admin admin, TypeAdmin typeAdmin) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin ne peut pas être null");
        }
        if (typeAdmin == null) {
            throw new IllegalArgumentException("TypeAdmin ne peut pas être null");
        }
        
        // Mettre à jour les permissions
        admin.setTypeAdmin(typeAdmin);
        
        try {
            // Sauvegarder en base
            adminDAO.update(admin);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour des permissions: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère tous les utilisateurs du système.
     * 
     * @param superAdmin Le super-admin qui consulte
     * @return Liste de tous les utilisateurs
     */
    public List<Utilisateur> voirTousLesUtilisateurs(SuperAdmin superAdmin) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        
        try {
            return utilisateurDAO.findAllActifs();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un rapport global sur tout le système.
     * 
     * @param superAdmin Le super-admin qui génère le rapport
     * @param periode La période couverte
     * @return Le rapport global généré
     */
    public Rapport genererRapportGlobal(SuperAdmin superAdmin, String periode) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        
        try {
            // Récupérer toutes les statistiques
            List<Utilisateur> utilisateurs = utilisateurDAO.findAllActifs();
            int totalUtilisateurs = utilisateurs.size();
            int totalDocuments = documentDAO.count();
            
            // Parser la période pour obtenir les dates
            LocalDate dateDebut = parsePeriode(periode, true);
            LocalDate dateFin = parsePeriode(periode, false);
            List<com.infinitpages.model.entity.Emprunt> emprunts = empruntDAO.findByPeriode(dateDebut, dateFin);
            int totalEmprunts = emprunts.size();
            
            // Calculer les revenus (simplifié - à améliorer avec PaiementDAO)
            double totalRevenus = 0.0; // TODO: Implémenter avec PaiementDAO
            
            // Compter les documents par type
            List<com.infinitpages.model.entity.Document> documents = documentDAO.findAll();
            long documentsReels = documents.stream()
                .filter(d -> d instanceof com.infinitpages.model.entity.DocumentReel)
                .count();
            long documentsNumeriques = documents.stream()
                .filter(d -> d instanceof com.infinitpages.model.entity.DocumentNumerique)
                .count();
            
            String contenu = String.format(
                "Rapport Global du Système - %s\n\n" +
                "=== Statistiques Générales ===\n" +
                "Total d'utilisateurs: %d\n" +
                "Total de documents: %d\n" +
                "Total d'emprunts: %d\n" +
                "Revenus totaux: %.2f €\n\n" +
                "=== Répartition par Type ===\n" +
                "Documents réels: %d\n" +
                "Documents numériques: %d\n",
                periode, totalUtilisateurs, totalDocuments, totalEmprunts, totalRevenus, 
                documentsReels, documentsNumeriques
            );
            
            Rapport rapport = new Rapport(
                "Rapport Global du Système",
                "GLOBAL",
                periode,
                superAdmin
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
     * Configure les paramètres globaux du système.
     * 
     * @param superAdmin Le super-admin qui configure
     * @param parametre Le nom du paramètre
     * @param valeur La nouvelle valeur
     */
    public void configurerSysteme(SuperAdmin superAdmin, String parametre, String valeur) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (parametre == null || parametre.isEmpty()) {
            throw new IllegalArgumentException("Le paramètre est requis");
        }
        
        // Pour l'instant, on log juste la configuration
        // TODO: Créer une table de configuration et un DAO si nécessaire
        // Configuration config = new Configuration(parametre, valeur);
        // configurationDAO.saveOrUpdate(config);
        System.out.println("Configuration système: " + parametre + " = " + valeur);
    }
    
    /**
     * Définit les tarifs des pénalités.
     * 
     * @param superAdmin Le super-admin qui configure
     * @param tarifParJour Le tarif par jour de retard
     */
    public void definirTarifsPenalites(SuperAdmin superAdmin, double tarifParJour) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (tarifParJour < 0) {
            throw new IllegalArgumentException("Le tarif ne peut pas être négatif");
        }
        
        // Sauvegarder le tarif dans la configuration
        configurerSysteme(superAdmin, "TARIF_PENALITE_PAR_JOUR", String.valueOf(tarifParJour));
    }
    
    /**
     * Modifie les durées d'emprunt par défaut.
     * 
     * @param superAdmin Le super-admin qui configure
     * @param typeUtilisateur Le type d'utilisateur
     * @param dureeJours La nouvelle durée en jours
     */
    public void modifierDureesEmprunt(SuperAdmin superAdmin, 
                                     TypeUtilisateur typeUtilisateur, 
                                     int dureeJours) {
        if (superAdmin == null) {
            throw new IllegalArgumentException("SuperAdmin ne peut pas être null");
        }
        if (typeUtilisateur == null) {
            throw new IllegalArgumentException("TypeUtilisateur ne peut pas être null");
        }
        if (dureeJours <= 0) {
            throw new IllegalArgumentException("La durée doit être positive");
        }
        
        // Sauvegarder la durée dans la configuration
        String cle = "DUREE_EMPRUNT_" + typeUtilisateur.name();
        configurerSysteme(superAdmin, cle, String.valueOf(dureeJours));
    }
    
    /**
     * Parse une période (ex: "Janvier 2025") en dates.
     */
    private LocalDate parsePeriode(String periode, boolean debut) {
        try {
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
            
            LocalDate now = LocalDate.now();
            if (debut) {
                return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            } else {
                return LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth());
            }
        } catch (Exception e) {
            LocalDate now = LocalDate.now();
            if (debut) {
                return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            } else {
                return LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth());
            }
        }
    }
    
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

