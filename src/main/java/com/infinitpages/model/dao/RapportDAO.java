package com.infinitpages.model.dao;

import com.infinitpages.model.entity.Rapport;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour la classe Rapport.
 */
public interface RapportDAO {
    
    /**
     * Trouve un rapport par son ID.
     * 
     * @param id L'identifiant du rapport
     * @return Optional contenant le rapport si trouvé
     */
    Optional<Rapport> findById(int id);
    
    /**
     * Trouve tous les rapports.
     * 
     * @return Liste de tous les rapports
     */
    List<Rapport> findAll();
    
    /**
     * Trouve tous les rapports d'un admin.
     * 
     * @param idAdmin L'identifiant de l'admin
     * @return Liste des rapports de l'admin
     */
    List<Rapport> findByAdmin(int idAdmin);
    
    /**
     * Trouve tous les rapports d'un type donné.
     * 
     * @param type Le type de rapport
     * @return Liste des rapports du type
     */
    List<Rapport> findByType(String type);
    
    /**
     * Trouve tous les rapports d'une période donnée.
     * 
     * @param periode La période (ex: "Janvier 2025")
     * @return Liste des rapports de la période
     */
    List<Rapport> findByPeriode(String periode);
    
    /**
     * Trouve tous les rapports d'un admin et d'un type.
     * 
     * @param idAdmin L'identifiant de l'admin
     * @param type Le type de rapport
     * @return Liste des rapports correspondants
     */
    List<Rapport> findByAdminAndType(int idAdmin, String type);
    
    /**
     * Sauvegarde un nouveau rapport.
     * 
     * @param rapport Le rapport à sauvegarder
     * @return Le rapport avec son ID généré
     */
    Rapport save(Rapport rapport);
    
    /**
     * Met à jour un rapport existant.
     * 
     * @param rapport Le rapport à mettre à jour
     * @return true si la mise à jour a réussi
     */
    boolean update(Rapport rapport);
    
    /**
     * Supprime un rapport par son ID.
     * 
     * @param id L'identifiant du rapport à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

