package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Document;
import com.infinitpages.util.constants.Genre;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Document.
 * Gère les opérations CRUD et de recherche sur les documents.
 */
public interface DocumentDAO {
    
    /**
     * Trouve un document par son ID.
     * 
     * @param id L'identifiant du document
     * @return Optional contenant le document si trouvé
     */
    Optional<Document> findById(int id);
    
    /**
     * Trouve tous les documents.
     * 
     * @return Liste de tous les documents
     */
    List<Document> findAll();
    
    /**
     * Trouve tous les documents disponibles.
     * 
     * @return Liste des documents disponibles
     */
    List<Document> findDisponibles();
    
    /**
     * Recherche des documents par titre (recherche partielle).
     * 
     * @param titre Le titre ou partie du titre
     * @return Liste des documents correspondants
     */
    List<Document> findByTitre(String titre);
    
    /**
     * Recherche des documents par auteur.
     * 
     * @param auteur Le nom de l'auteur
     * @return Liste des documents de l'auteur
     */
    List<Document> findByAuteur(String auteur);
    
    /**
     * Recherche des documents par genre.
     * 
     * @param genre Le genre
     * @return Liste des documents du genre
     */
    List<Document> findByGenre(Genre genre);
    
    /**
     * Recherche des documents par catégorie.
     * 
     * @param idCategorie L'identifiant de la catégorie
     * @return Liste des documents de la catégorie
     */
    List<Document> findByCategorie(int idCategorie);
    
    /**
     * Recherche textuelle dans titre, auteur, résumé et mots-clés.
     * 
     * @param recherche Le terme de recherche
     * @return Liste des documents correspondants
     */
    List<Document> search(String recherche);
    
    /**
     * Trouve les documents les plus consultés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les plus consultés
     */
    List<Document> findMostConsulted(int limit);
    
    /**
     * Trouve les documents les plus empruntés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les plus empruntés
     */
    List<Document> findMostBorrowed(int limit);
    
    /**
     * Trouve les documents les mieux notés.
     * 
     * @param limit Nombre maximum de résultats
     * @return Liste des documents les mieux notés
     */
    List<Document> findBestRated(int limit);
    
    /**
     * Sauvegarde un nouveau document.
     * 
     * @param document Le document à sauvegarder
     * @return Le document avec son ID généré
     */
    Document save(Document document);
    
    /**
     * Met à jour un document existant.
     * 
     * @param document Le document à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Document document);
    
    /**
     * Supprime un document par son ID.
     * 
     * @param id L'identifiant du document à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
    
    /**
     * Incrémente le compteur de consultations.
     * 
     * @param idDocument L'identifiant du document
     * @return true si la mise à jour a réussi
     */
    boolean incrementerConsultations(int idDocument);
    
    /**
     * Incrémente le compteur d'emprunts.
     * 
     * @param idDocument L'identifiant du document
     * @return true si la mise à jour a réussi
     */
    boolean incrementerEmprunts(int idDocument);
    
    /**
     * Met à jour la note globale du document.
     * 
     * @param idDocument L'identifiant du document
     * @param noteGlobale La nouvelle note globale
     * @return true si la mise à jour a réussi
     */
    boolean updateNoteGlobale(int idDocument, double noteGlobale);
    
    /**
     * Change la disponibilité d'un document.
     * 
     * @param idDocument L'identifiant du document
     * @param disponible La nouvelle disponibilité
     * @return true si la mise à jour a réussi
     */
    boolean setDisponible(int idDocument, boolean disponible);
    
    /**
     * Compte le nombre total de documents.
     * 
     * @return Le nombre de documents
     */
    int count();
    
    /**
     * Compte le nombre de documents disponibles.
     * 
     * @return Le nombre de documents disponibles
     */
    int countDisponibles();
}

