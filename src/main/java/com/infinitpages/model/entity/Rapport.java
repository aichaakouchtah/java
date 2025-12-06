package com.infinitpages.model.entity;

import java.time.LocalDateTime;

/**
 * Classe représentant un rapport statistique généré pour les admins.
 */
public class Rapport {
    
    // Identifiant
    protected int id;
    
    // Titre du rapport
    protected String titre;
    
    // Date de création
    protected LocalDateTime dateGeneration;
    
    // Contenu du rapport
    protected String contenu;
    
    // Type ("EMPRUNTS", "CONSULTATIONS", "UTILISATEURS")
    protected String type;
    
    // Période couverte ("Janvier 2025", "2024")
    protected String periode;
    
    // Relations : Référence à l'admin qui a généré le rapport
    protected Admin admin;
    
    // Constructeurs
    public Rapport() {
        this.dateGeneration = LocalDateTime.now();
    }
    
    public Rapport(String titre, String type, String periode, Admin admin) {
        this();
        this.titre = titre;
        this.type = type;
        this.periode = periode;
        this.admin = admin;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
    
    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPeriode() {
        return periode;
    }
    
    public void setPeriode(String periode) {
        this.periode = periode;
    }
    
    public Admin getAdmin() {
        return admin;
    }
    
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}

