package com.infinitpages.model.entity;

/**
 * Classe représentant le Super Administrateur du système.
 * Hérite de Admin et supervise tous les admins et configure le système global.
 */
public class SuperAdmin extends Admin {
    
    // Les permissions sont héritées de Admin
    // Le SuperAdmin a tous les droits d'administration
    
    // Constructeurs
    public SuperAdmin() {
        super();
    }
    
    public SuperAdmin(String nom, String email, String motDePasse, String departement) {
        super(nom, email, motDePasse, departement, com.infinitpages.util.constants.TypeAdmin.BOTH);
    }
}

