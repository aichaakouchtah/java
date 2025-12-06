package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de EmpruntDAO.
 */
public class EmpruntDAOImpl implements EmpruntDAO {
    
    @Override
    public Optional<Emprunt> findById(int id) {
        String sql = "SELECT * FROM emprunt WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'emprunt par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Emprunt> findAll() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les emprunts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByUtilisateur(int idUtilisateur) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE id_utilisateur = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByDocument(int idDocument) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE id_document = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDocument);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par document: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findByEtat(String etat) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE etat = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etat);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par état: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findActifsByUtilisateur(int idUtilisateur) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE id_utilisateur = ? AND etat = 'EN_COURS' ORDER BY date_retour ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts actifs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE etat = 'EN_COURS' AND date_retour < CURDATE() ORDER BY date_retour ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts en retard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public List<Emprunt> findEnRetardByUtilisateur(int idUtilisateur) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE id_utilisateur = ? AND etat = 'EN_COURS' AND date_retour < CURDATE() ORDER BY date_retour ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts en retard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public int countEmpruntsActifs(int idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM emprunt WHERE id_utilisateur = ? AND etat = 'EN_COURS'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des emprunts actifs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<Emprunt> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE date_emprunt BETWEEN ? AND ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprunts.add(mapResultSetToEmprunt(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par période: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    @Override
    public Emprunt save(Emprunt emprunt) {
        String sql = "INSERT INTO emprunt (id_utilisateur, id_document, date_emprunt, date_retour, " +
                     "date_retour_effective, etat, duree_max, penalite, statut, montant_paye, date_paiement) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, emprunt.getUtilisateur().getId());
            stmt.setInt(2, emprunt.getDocument().getId());
            stmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            
            if (emprunt.getDateRetourEffective() != null) {
                stmt.setDate(5, Date.valueOf(emprunt.getDateRetourEffective()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, emprunt.getEtat());
            stmt.setInt(7, emprunt.getDureeMax());
            stmt.setDouble(8, emprunt.getPenalite());
            stmt.setString(9, emprunt.getStatut());
            stmt.setDouble(10, emprunt.getMontantPaye());
            
            if (emprunt.getDatePaiement() != null) {
                stmt.setDate(11, Date.valueOf(emprunt.getDatePaiement()));
            } else {
                stmt.setNull(11, Types.DATE);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        emprunt.setId(generatedKeys.getInt(1));
                        return emprunt;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde de l'emprunt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Emprunt emprunt) {
        String sql = "UPDATE emprunt SET id_utilisateur = ?, id_document = ?, date_emprunt = ?, " +
                     "date_retour = ?, date_retour_effective = ?, etat = ?, duree_max = ?, " +
                     "penalite = ?, statut = ?, montant_paye = ?, date_paiement = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, emprunt.getUtilisateur().getId());
            stmt.setInt(2, emprunt.getDocument().getId());
            stmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            
            if (emprunt.getDateRetourEffective() != null) {
                stmt.setDate(5, Date.valueOf(emprunt.getDateRetourEffective()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, emprunt.getEtat());
            stmt.setInt(7, emprunt.getDureeMax());
            stmt.setDouble(8, emprunt.getPenalite());
            stmt.setString(9, emprunt.getStatut());
            stmt.setDouble(10, emprunt.getMontantPaye());
            
            if (emprunt.getDatePaiement() != null) {
                stmt.setDate(11, Date.valueOf(emprunt.getDatePaiement()));
            } else {
                stmt.setNull(11, Types.DATE);
            }
            
            stmt.setInt(12, emprunt.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'emprunt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM emprunt WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'emprunt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Emprunt.
     */
    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        
        emprunt.setId(rs.getInt("id"));
        
        // Créer des objets Utilisateur et Document avec juste l'ID
        // Les DAO correspondants devront être utilisés pour charger les objets complets si nécessaire
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id_utilisateur"));
        emprunt.setUtilisateur(utilisateur);
        
        Document document = new Document() {};
        document.setId(rs.getInt("id_document"));
        emprunt.setDocument(document);
        
        Date dateEmprunt = rs.getDate("date_emprunt");
        if (dateEmprunt != null) {
            emprunt.setDateEmprunt(dateEmprunt.toLocalDate());
        }
        
        Date dateRetour = rs.getDate("date_retour");
        if (dateRetour != null) {
            emprunt.setDateRetour(dateRetour.toLocalDate());
        }
        
        Date dateRetourEffective = rs.getDate("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective.toLocalDate());
        }
        
        emprunt.setEtat(rs.getString("etat"));
        emprunt.setDureeMax(rs.getInt("duree_max"));
        emprunt.setPenalite(rs.getDouble("penalite"));
        emprunt.setStatut(rs.getString("statut"));
        emprunt.setMontantPaye(rs.getDouble("montant_paye"));
        
        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            emprunt.setDatePaiement(datePaiement.toLocalDate());
        }
        
        return emprunt;
    }
}

