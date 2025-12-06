package com.infinitpages.model.entity;

import java.util.List;

/**
 * Classe représentant une catégorie de documents.
 * Organise les documents en catégories (Science, Littérature, Histoire...).
 */
public class Categorie {
    
    // Identifiant
    protected int id;
    
    // Nom de la catégorie (ex: "Romans", "Informatique")
    protected String nom;
    
    // Description de la catégorie
    protected String description;
    
    // Combien de documents dans cette catégorie
    protected int nombreDocuments;
    
    // Relations : Liste des documents de cette catégorie
    protected List<Document> documents;
    
    // Relations : Catégorie parente (pour hiérarchie)
    protected Categorie categorieParente;
    
    // Constructeurs
    public Categorie() {
        this.nombreDocuments = 0;
    }
    
    public Categorie(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getNombreDocuments() {
        return nombreDocuments;
    }
    
    public void setNombreDocuments(int nombreDocuments) {
        this.nombreDocuments = nombreDocuments;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public Categorie getCategorieParente() {
        return categorieParente;
    }
    
    public void setCategorieParente(Categorie categorieParente) {
        this.categorieParente = categorieParente;
    }
}

