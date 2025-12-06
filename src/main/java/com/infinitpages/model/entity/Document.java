package com.infinitpages.model.entity;

import com.infinitpages.util.constants.Genre;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe abstraite représentant un document dans la bibliothèque.
 * Classe mère pour DocumentReel et DocumentNumerique.
 */
public abstract class Document {
    
    // Identifiant unique du document
    protected int id;
    
    // Titre du document
    protected String titre;
    
    // Auteur du document
    protected String auteur;
    
    // Catégorie du document (nom de la catégorie)
    protected String categorie;
    
    // Mots-clés pour la recherche
    protected List<String> motsCles;
    
    // Le document est-il disponible pour emprunt ?
    protected boolean disponible;
    
    // Format du document (ex: "Livre", "Article", "Manga")
    protected String format;
    
    // Date de publication
    protected LocalDate datePublication;
    
    // Résumé/description du document
    protected String resume;
    
    // Nombre de fois que le document a été consulté
    protected int nombreConsultations;
    
    // Nombre de fois que le document a été emprunté
    protected int nombreEmprunts;
    
    // Note globale moyenne (calculée à partir des avis)
    protected double noteGlobale;
    
    // Prix par jour pour l'emprunt
    protected double prixParJour;
    
    // Relations : Catégorie à laquelle appartient le document
    protected Categorie categorieEntity;
    
    // Constructeurs
    public Document() {
        this.disponible = true;
        this.nombreConsultations = 0;
        this.nombreEmprunts = 0;
        this.noteGlobale = 0.0;
        this.prixParJour = 0.0;
    }
    
    public Document(String titre, String auteur, String format) {
        this();
        this.titre = titre;
        this.auteur = auteur;
        this.format = format;
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
    
    public String getAuteur() {
        return auteur;
    }
    
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    public List<String> getMotsCles() {
        return motsCles;
    }
    
    public void setMotsCles(List<String> motsCles) {
        this.motsCles = motsCles;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public LocalDate getDatePublication() {
        return datePublication;
    }
    
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }
    
    public String getResume() {
        return resume;
    }
    
    public void setResume(String resume) {
        this.resume = resume;
    }
    
    public int getNombreConsultations() {
        return nombreConsultations;
    }
    
    public void setNombreConsultations(int nombreConsultations) {
        this.nombreConsultations = nombreConsultations;
    }
    
    public int getNombreEmprunts() {
        return nombreEmprunts;
    }
    
    public void setNombreEmprunts(int nombreEmprunts) {
        this.nombreEmprunts = nombreEmprunts;
    }
    
    public double getNoteGlobale() {
        return noteGlobale;
    }
    
    public void setNoteGlobale(double noteGlobale) {
        this.noteGlobale = noteGlobale;
    }
    
    public double getPrixParJour() {
        return prixParJour;
    }
    
    public void setPrixParJour(double prixParJour) {
        this.prixParJour = prixParJour;
    }
    
    public Categorie getCategorieEntity() {
        return categorieEntity;
    }
    
    public void setCategorieEntity(Categorie categorieEntity) {
        this.categorieEntity = categorieEntity;
    }
}

