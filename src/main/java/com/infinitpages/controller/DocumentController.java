package com.infinitpages.controller;

import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.DocumentNumerique;
import com.infinitpages.model.entity.DocumentReel;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.dao.CategorieDAO;
import com.infinitpages.model.dao.impl.DocumentDAOImpl;
import com.infinitpages.model.dao.impl.CategorieDAOImpl;
import com.infinitpages.util.constants.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des documents (pour les utilisateurs).
 * Coordonne entre la View et les DAO.
 * 
 * Responsabilités :
 * - Recherche de documents
 * - Consultation de documents
 * - Affichage des détails
 * - Filtrage par catégorie, genre, auteur
 * - Incrémenter les consultations
 */
public class DocumentController {
    
    private DocumentDAO documentDAO;
    private CategorieDAO categorieDAO;
    private Utilisateur utilisateurConnecte; // L'utilisateur actuellement connecté
    // TODO: Injecter la vue quand elle sera créée
    // private DocumentView view;
    
    /**
     * Constructeur par défaut.
     */
    public DocumentController() {
        this.documentDAO = new DocumentDAOImpl();
        this.categorieDAO = new CategorieDAOImpl();
    }
    
    /**
     * Constructeur avec injection des DAO (pour les tests).
     * 
     * @param documentDAO Le DAO Document à utiliser
     * @param categorieDAO Le DAO Categorie à utiliser
     */
    public DocumentController(DocumentDAO documentDAO, CategorieDAO categorieDAO) {
        this.documentDAO = documentDAO;
        this.categorieDAO = categorieDAO;
    }
    
    /**
     * Définit l'utilisateur connecté.
     * 
     * @param utilisateur L'utilisateur connecté
     */
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }
    
    /**
     * Récupère l'utilisateur connecté.
     * 
     * @return L'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Recherche des documents par texte (titre, auteur, résumé, mots-clés).
     * 
     * @param recherche Le terme de recherche
     * @return Liste des documents correspondants
     */
    public List<Document> rechercherDocuments(String recherche) {
        if (recherche == null || recherche.isEmpty()) {
            // TODO: view.showError("Veuillez entrer un terme de recherche");
            return List.of();
        }
        
        try {
            List<Document> documents = documentDAO.search(recherche);
            // TODO: view.afficherResultatsRecherche(documents);
            return documents;
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la recherche : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Recherche des documents par titre.
     * 
     * @param titre Le titre ou partie du titre
     * @return Liste des documents correspondants
     */
    public List<Document> rechercherParTitre(String titre) {
        if (titre == null || titre.isEmpty()) {
            return List.of();
        }
        
        try {
            return documentDAO.findByTitre(titre);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la recherche : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Recherche des documents par auteur.
     * 
     * @param auteur Le nom de l'auteur
     * @return Liste des documents de l'auteur
     */
    public List<Document> rechercherParAuteur(String auteur) {
        if (auteur == null || auteur.isEmpty()) {
            return List.of();
        }
        
        try {
            return documentDAO.findByAuteur(auteur);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la recherche : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Recherche des documents par catégorie.
     * 
     * @param idCategorie L'identifiant de la catégorie
     * @return Liste des documents de la catégorie
     */
    public List<Document> rechercherParCategorie(int idCategorie) {
        try {
            return documentDAO.findByCategorie(idCategorie);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la recherche : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Recherche des documents par genre.
     * 
     * @param genre Le genre
     * @return Liste des documents du genre
     */
    public List<Document> rechercherParGenre(Genre genre) {
        if (genre == null) {
            return List.of();
        }
        
        try {
            return documentDAO.findByGenre(genre);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la recherche : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère tous les documents disponibles.
     * 
     * @return Liste des documents disponibles
     */
    public List<Document> getDocumentsDisponibles() {
        try {
            return documentDAO.findDisponibles();
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère tous les documents.
     * 
     * @return Liste de tous les documents
     */
    public List<Document> getAllDocuments() {
        try {
            return documentDAO.findAll();
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère un document par son ID et incrémente le compteur de consultations.
     * 
     * @param idDocument L'identifiant du document
     * @return Le document trouvé
     */
    public Optional<Document> consulterDocument(int idDocument) {
        try {
            Optional<Document> docOpt = documentDAO.findById(idDocument);
            
            if (docOpt.isPresent()) {
                Document document = docOpt.get();
                
                // Incrémenter le compteur de consultations
                documentDAO.incrementerConsultations(idDocument);
                
                // TODO: view.afficherDetailsDocument(document);
                return Optional.of(document);
            } else {
                // TODO: view.showError("Document introuvable");
                return Optional.empty();
            }
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la consultation : " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Récupère les documents les plus consultés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les plus consultés
     */
    public List<Document> getDocumentsPlusConsultes(int limit) {
        try {
            return documentDAO.findMostConsulted(limit);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les documents les plus empruntés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les plus empruntés
     */
    public List<Document> getDocumentsPlusEmpruntes(int limit) {
        try {
            return documentDAO.findMostBorrowed(limit);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les documents les mieux notés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les mieux notés
     */
    public List<Document> getDocumentsMieuxNotes(int limit) {
        try {
            return documentDAO.findBestRated(limit);
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère toutes les catégories.
     * 
     * @return Liste de toutes les catégories
     */
    public List<Categorie> getAllCategories() {
        try {
            return categorieDAO.findAll();
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère les catégories racines (sans parent).
     * 
     * @return Liste des catégories racines
     */
    public List<Categorie> getCategoriesRacines() {
        try {
            return categorieDAO.findRootCategories();
        } catch (Exception e) {
            // TODO: view.showError("Erreur lors de la récupération : " + e.getMessage());
            return List.of();
        }
    }
}

