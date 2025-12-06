package com.infinitpages.model.entity;

/**
 * Classe représentant les documents électroniques (PDF, ebooks, articles en ligne).
 * Hérite de Document.
 */
public class DocumentNumerique extends Document {
    
    // Lien vers le fichier
    protected String url;
    
    // Taille du fichier en Mo
    protected double taille;
    
    // Type de fichier (PDF, EPUB, DOCX...)
    protected String formatFichier;
    
    // Peut-on le télécharger ou juste le consulter en ligne ?
    protected boolean telechargeable;
    
    // Constructeurs
    public DocumentNumerique() {
        super();
        this.telechargeable = false;
    }
    
    public DocumentNumerique(String titre, String auteur, com.infinitpages.util.constants.Genre genre, String format) {
        super(titre, auteur, format);
        this.telechargeable = false;
    }
    
    public DocumentNumerique(String titre, String auteur, com.infinitpages.util.constants.Genre genre, String format,
                            String url, double taille, String formatFichier, boolean telechargeable) {
        super(titre, auteur, format);
        this.url = url;
        this.taille = taille;
        this.formatFichier = formatFichier;
        this.telechargeable = telechargeable;
    }
    
    // Getters et Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public double getTaille() {
        return taille;
    }
    
    public void setTaille(double taille) {
        this.taille = taille;
    }
    
    public String getFormatFichier() {
        return formatFichier;
    }
    
    public void setFormatFichier(String formatFichier) {
        this.formatFichier = formatFichier;
    }
    
    public boolean isTelechargeable() {
        return telechargeable;
    }
    
    public void setTelechargeable(boolean telechargeable) {
        this.telechargeable = telechargeable;
    }
}

