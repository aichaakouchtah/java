package com.infinitpages.model.entity;

import com.infinitpages.util.constants.TypeAdmin;

/**
 * Classe représentant le Super Administrateur du système.
 * Hérite de Admin et supervise tous les admins et configure le système global.
 * Le SuperAdmin a automatiquement tous les droits (BOTH).
 */
public class SuperAdmin extends Admin {
    
    // Les permissions sont héritées de Admin
    // Le SuperAdmin a tous les droits d'administration
    
    // Constructeurs
    public SuperAdmin() {
        super();
        // Le SuperAdmin a toujours tous les droits
        this.setTypeAdmin(TypeAdmin.BOTH);
    }
    
    public SuperAdmin(String nom, String email, String motDePasse, String departement) {
<<<<<<< HEAD
        super(nom, email, motDePasse, departement, TypeAdmin.BOTH);
    }
    
    /**
     * Vérifie si cette instance est un SuperAdmin
     * 
     * @return toujours true pour SuperAdmin
     */
    public boolean estSuperAdmin() {
        return true;
    }
    
    /**
     * Le SuperAdmin peut toujours gérer les documents réels
     * 
     * @return toujours true
     */
    @Override
    public boolean peutGererReels() {
        return true;
    }
    
    /**
     * Le SuperAdmin peut toujours gérer les documents numériques
     * 
     * @return toujours true
     */
    @Override
    public boolean peutGererNumeriques() {
        return true;
    }
    
    /**
     * Le SuperAdmin peut toujours gérer les deux types
     * 
     * @return toujours true
     */
    @Override
    public boolean peutGererLesDeux() {
        return true;
    }
    
    /**
     * Le SuperAdmin a toujours des permissions
     * 
     * @return toujours true
     */
    @Override
    public boolean aDesPermissions() {
        return true;
    }
    
    /**
     * Empêche la modification du typeAdmin pour un SuperAdmin
     * Le SuperAdmin a toujours BOTH
     * 
     * @param typeAdmin Le type à définir (ignoré pour SuperAdmin)
     */
    @Override
    public void setTypeAdmin(TypeAdmin typeAdmin) {
        // Le SuperAdmin garde toujours BOTH, on ignore la modification
        super.setTypeAdmin(TypeAdmin.BOTH);
    }
    
    /**
     * Vérifie si le SuperAdmin peut gérer un autre admin
     * 
     * @param admin L'admin à vérifier
     * @return true si l'admin n'est pas null et n'est pas lui-même
     */
    public boolean peutGererAdmin(Admin admin) {
        if (admin == null) {
            return false;
        }
        // Un SuperAdmin ne peut pas se gérer lui-même
        if (admin.getId() == this.getId() && admin instanceof SuperAdmin) {
            return false;
        }
        return true;
    }
    
    /**
     * Vérifie si le SuperAdmin peut supprimer un admin
     * 
     * @param admin L'admin à supprimer
     * @return true si l'admin peut être supprimé
     */
    public boolean peutSupprimerAdmin(Admin admin) {
        if (admin == null) {
            return false;
        }
        // Ne peut pas se supprimer lui-même
        if (admin.getId() == this.getId()) {
            return false;
        }
        // Ne peut pas supprimer un autre SuperAdmin
        if (admin instanceof SuperAdmin) {
            return false;
        }
        return true;
    }
    
    /**
     * Retourne un résumé du SuperAdmin (pour affichage)
     * 
     * @return Résumé du SuperAdmin
     */
    @Override
    public String getResume() {
        return String.format("SuperAdmin: %s (%s) - Département: %s - Tous les droits",
            nom != null ? nom : "N/A",
            email != null ? email : "N/A",
            departement != null ? departement : "N/A"
        );
=======
        super(nom, email, motDePasse, departement, com.infinitpages.util.constants.TypeAdmin.BOTH);
>>>>>>> 5bdd9658f3ab6c81eb9a9f78a2c7b95b44c05455
    }
}

