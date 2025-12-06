package com.infinitpages.model.entity;

/**
 * Classe représentant les documents physiques (livres réels) dans la bibliothèque.
 * Hérite de Document.
 */
public class DocumentReel extends Document {
    
    // Où se trouve le livre (ex: "Rayon A, Étagère 3")
    protected String emplacement;
    
    // État du livre ("Neuf", "Bon état", "Abîmé")
    protected String condition;
    
    // Code ISBN du livre
    protected String ISBN;
    
    // Constructeurs
    public DocumentReel() {
        super();
    }
    
    public DocumentReel(String titre, String auteur, com.infinitpages.util.constants.Genre genre, String format) {
        super(titre, auteur, format);
    }
    
    public DocumentReel(String titre, String auteur, com.infinitpages.util.constants.Genre genre, String format, 
                       String emplacement, String condition, String ISBN) {
        super(titre, auteur, format);
        this.emplacement = emplacement;
        this.condition = condition;
        this.ISBN = ISBN;
    }
    
    // Getters et Setters
    public String getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getISBN() {
        return ISBN;
    }
    
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
}

