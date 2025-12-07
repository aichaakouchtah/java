package com.infinitpages.model.dao.impl;

import com.infinitpages.model.dao.PaiementDAO;
import com.infinitpages.model.entity.Emprunt;
import com.infinitpages.model.entity.Paiement;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de PaiementDAO.
 */
public class PaiementDAOImpl implements PaiementDAO {
    
    @Override
    public Optional<Paiement> findById(int id) {
        String sql = "SELECT * FROM paiement WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de paiement par ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement ORDER BY date_paiement DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les paiements: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paiements;
    }
    
    @Override
    public List<Paiement> findByUtilisateur(int idUtilisateur) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE id_utilisateur = ? ORDER BY date_paiement DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paiements;
    }
    
    @Override
    public List<Paiement> findByEmprunt(int idEmprunt) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE id_emprunt = ? ORDER BY date_paiement DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEmprunt);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par emprunt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paiements;
    }
    
    @Override
    public List<Paiement> findByStatut(String statut) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE statut = ? ORDER BY date_paiement DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, statut);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par statut: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paiements;
    }
    
    @Override
    public List<Paiement> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE date_paiement BETWEEN ? AND ? ORDER BY date_paiement DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par période: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paiements;
    }
    
    @Override
    public Optional<Paiement> findByReference(String reference) {
        String sql = "SELECT * FROM paiement WHERE reference = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reference);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPaiement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par référence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public double getTotalPaiements(int idUtilisateur) {
        String sql = "SELECT SUM(montant) FROM paiement WHERE id_utilisateur = ? AND statut = 'VALIDE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total des paiements: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    @Override
    public Paiement save(Paiement paiement) {
        String sql = "INSERT INTO paiement (id_utilisateur, id_emprunt, montant, date_paiement, " +
                     "methode_paiement, statut, reference, motif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, paiement.getUtilisateur().getId());
            
            if (paiement.getEmprunt() != null) {
                stmt.setInt(2, paiement.getEmprunt().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setDouble(3, paiement.getMontant());
            stmt.setDate(4, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(5, paiement.getMethodePaiement());
            stmt.setString(6, paiement.getStatut());
            stmt.setString(7, paiement.getReference());
            stmt.setString(8, paiement.getMotif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        paiement.setId(generatedKeys.getInt(1));
                        return paiement;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Paiement paiement) {
        String sql = "UPDATE paiement SET id_utilisateur = ?, id_emprunt = ?, montant = ?, " +
                     "date_paiement = ?, methode_paiement = ?, statut = ?, reference = ?, motif = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, paiement.getUtilisateur().getId());
            
            if (paiement.getEmprunt() != null) {
                stmt.setInt(2, paiement.getEmprunt().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setDouble(3, paiement.getMontant());
            stmt.setDate(4, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(5, paiement.getMethodePaiement());
            stmt.setString(6, paiement.getStatut());
            stmt.setString(7, paiement.getReference());
            stmt.setString(8, paiement.getMotif());
            stmt.setInt(9, paiement.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM paiement WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mappe un ResultSet vers un objet Paiement.
     */
    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        
        paiement.setId(rs.getInt("id"));
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id_utilisateur"));
        paiement.setUtilisateur(utilisateur);
        
        int idEmprunt = rs.getInt("id_emprunt");
        if (!rs.wasNull() && idEmprunt > 0) {
            Emprunt emprunt = new Emprunt();
            emprunt.setId(idEmprunt);
            paiement.setEmprunt(emprunt);
        }
        
        paiement.setMontant(rs.getDouble("montant"));
        
        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            paiement.setDatePaiement(datePaiement.toLocalDate());
        }
        
        paiement.setMethodePaiement(rs.getString("methode_paiement"));
        paiement.setStatut(rs.getString("statut"));
        paiement.setReference(rs.getString("reference"));
        paiement.setMotif(rs.getString("motif"));
        
        return paiement;
    }
}

