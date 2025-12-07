-- =====================================================
-- Script de création de la base de données InfinitPages
-- Bibliothèque Numérique
-- =====================================================

-- Supprimer les tables si elles existent (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS historique_document;
DROP TABLE IF EXISTS rapport;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS avis;
DROP TABLE IF EXISTS paiement;
DROP TABLE IF EXISTS emprunt;
DROP TABLE IF EXISTS document_numerique;
DROP TABLE IF EXISTS document_reel;
DROP TABLE IF EXISTS document;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS superadmin;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS personne;

-- =====================================================
-- TABLE PERSONNE (classe de base)
-- =====================================================
CREATE TABLE personne (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    date_inscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    est_actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_est_actif (est_actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE UTILISATEUR (hérite de Personne)
-- =====================================================
CREATE TABLE utilisateur (
    id_personne INT PRIMARY KEY,
    type_utilisateur ENUM('PERSONNE_NORMALE', 'ETUDIANT', 'ENSEIGNANT') NOT NULL,
    limite_emprunts INT NOT NULL DEFAULT 3,
    solde_a_payer DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (id_personne) REFERENCES personne(id) ON DELETE CASCADE,
    INDEX idx_type_utilisateur (type_utilisateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE ADMIN (hérite de Personne)
-- =====================================================
CREATE TABLE admin (
    id_personne INT PRIMARY KEY,
    type_admin ENUM('REEL_ONLY', 'NUMERIQUE_ONLY', 'BOTH') NOT NULL,
    departement VARCHAR(255),
    FOREIGN KEY (id_personne) REFERENCES personne(id) ON DELETE CASCADE,
    INDEX idx_type_admin (type_admin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE SUPERADMIN (hérite de Admin)
-- =====================================================
CREATE TABLE superadmin (
    id_admin INT PRIMARY KEY,
    FOREIGN KEY (id_admin) REFERENCES admin(id_personne) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE CATEGORIE
-- =====================================================
CREATE TABLE categorie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    nombre_documents INT NOT NULL DEFAULT 0,
    id_categorie_parente INT,
    FOREIGN KEY (id_categorie_parente) REFERENCES categorie(id) ON DELETE SET NULL,
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE DOCUMENT (classe de base)
-- =====================================================
CREATE TABLE document (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(500) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    genre ENUM('ARTICLE', 'NOVEL', 'LIVRE_SCIENTIFIQUE', 'MANGA', 'BANDE_DESSINEE', 
               'MAGAZINE', 'THESE', 'MANUEL', 'BIOGRAPHIE', 'ESSAI', 'POESIE', 'THEATRE', 'AUTRE') NOT NULL,
    format VARCHAR(100),
    date_publication DATE,
    resume TEXT,
    mots_cles TEXT,
    prix_par_jour DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    nombre_consultations INT NOT NULL DEFAULT 0,
    nombre_emprunts INT NOT NULL DEFAULT 0,
    note_globale DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    id_categorie INT,
    FOREIGN KEY (id_categorie) REFERENCES categorie(id) ON DELETE SET NULL,
    INDEX idx_titre (titre(255)),
    INDEX idx_auteur (auteur),
    INDEX idx_genre (genre),
    INDEX idx_disponible (disponible),
    INDEX idx_categorie (id_categorie),
    FULLTEXT INDEX idx_recherche (titre, auteur, resume, mots_cles)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE DOCUMENT_REEL (hérite de Document)
-- =====================================================
CREATE TABLE document_reel (
    id_document INT PRIMARY KEY,
    emplacement VARCHAR(255),
    condition_livre VARCHAR(100),
    isbn VARCHAR(20),
    FOREIGN KEY (id_document) REFERENCES document(id) ON DELETE CASCADE,
    INDEX idx_isbn (isbn),
    INDEX idx_emplacement (emplacement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE DOCUMENT_NUMERIQUE (hérite de Document)
-- =====================================================
CREATE TABLE document_numerique (
    id_document INT PRIMARY KEY,
    url VARCHAR(500),
    chemin_fichier VARCHAR(1000),
    taille DECIMAL(10, 2),
    format_fichier VARCHAR(50),
    hash_fichier VARCHAR(64),
    telechargeable BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_document) REFERENCES document(id) ON DELETE CASCADE,
    INDEX idx_format_fichier (format_fichier),
    INDEX idx_telechargeable (telechargeable)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE EMPRUNT
-- =====================================================
CREATE TABLE emprunt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_document INT NOT NULL,
    date_emprunt DATE NOT NULL,
    date_retour DATE NOT NULL,
    date_retour_effective DATE,
    etat ENUM('EN_COURS', 'RETOURNE', 'EN_RETARD') NOT NULL DEFAULT 'EN_COURS',
    duree_max INT NOT NULL DEFAULT 14,
    penalite DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    statut ENUM('PAYE', 'NON_PAYE') NOT NULL DEFAULT 'NON_PAYE',
    montant_paye DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    date_paiement DATE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_personne) ON DELETE CASCADE,
    FOREIGN KEY (id_document) REFERENCES document(id) ON DELETE CASCADE,
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_document (id_document),
    INDEX idx_etat (etat),
    INDEX idx_date_retour (date_retour),
    INDEX idx_date_emprunt (date_emprunt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE PAIEMENT
-- =====================================================
CREATE TABLE paiement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_emprunt INT,
    montant DECIMAL(10, 2) NOT NULL,
    date_paiement DATE NOT NULL,
    methode_paiement ENUM('ESPECES', 'CARTE', 'CHEQUE', 'VIREMENT') NOT NULL,
    statut ENUM('EN_ATTENTE', 'VALIDE', 'ANNULE', 'REFUSE') NOT NULL DEFAULT 'EN_ATTENTE',
    reference VARCHAR(100),
    motif VARCHAR(255),
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_personne) ON DELETE CASCADE,
    FOREIGN KEY (id_emprunt) REFERENCES emprunt(id) ON DELETE SET NULL,
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_emprunt (id_emprunt),
    INDEX idx_statut (statut),
    INDEX idx_date_paiement (date_paiement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE HISTORIQUE
-- =====================================================
CREATE TABLE historique (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_personne INT NOT NULL,
    type ENUM('HISTORIQUE', 'LISTE_LECTURE') NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    est_partage BOOLEAN NOT NULL DEFAULT FALSE,
    details TEXT,
    FOREIGN KEY (id_personne) REFERENCES personne(id) ON DELETE CASCADE,
    INDEX idx_personne (id_personne),
    INDEX idx_type (type),
    INDEX idx_date_creation (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE HISTORIQUE_DOCUMENT (relation many-to-many)
-- =====================================================
CREATE TABLE historique_document (
    id_historique INT NOT NULL,
    id_document INT NOT NULL,
    date_ajout DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_historique, id_document),
    FOREIGN KEY (id_historique) REFERENCES historique(id) ON DELETE CASCADE,
    FOREIGN KEY (id_document) REFERENCES document(id) ON DELETE CASCADE,
    INDEX idx_document (id_document)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE AVIS
-- =====================================================
CREATE TABLE avis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_document INT NOT NULL,
    contenu TEXT NOT NULL,
    note INT NOT NULL CHECK (note >= 1 AND note <= 5),
    date_avis DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    est_modere BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_personne) ON DELETE CASCADE,
    FOREIGN KEY (id_document) REFERENCES document(id) ON DELETE CASCADE,
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_document (id_document),
    INDEX idx_est_modere (est_modere),
    INDEX idx_note (note),
    UNIQUE KEY unique_avis_utilisateur_document (id_utilisateur, id_document)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE NOTIFICATION
-- =====================================================
CREATE TABLE notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_destinataire INT NOT NULL,
    message TEXT NOT NULL,
    date_envoi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_lecture DATETIME,
    est_lue BOOLEAN NOT NULL DEFAULT FALSE,
    type ENUM('RAPPEL', 'ALERTE', 'INFO') NOT NULL,
    priorite ENUM('HAUTE', 'MOYENNE', 'BASSE') NOT NULL DEFAULT 'MOYENNE',
    FOREIGN KEY (id_destinataire) REFERENCES personne(id) ON DELETE CASCADE,
    INDEX idx_destinataire (id_destinataire),
    INDEX idx_est_lue (est_lue),
    INDEX idx_type (type),
    INDEX idx_date_envoi (date_envoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE RAPPORT
-- =====================================================
CREATE TABLE rapport (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_admin INT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    date_generation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contenu TEXT,
    type VARCHAR(100) NOT NULL,
    periode VARCHAR(100),
    FOREIGN KEY (id_admin) REFERENCES admin(id_personne) ON DELETE CASCADE,
    INDEX idx_admin (id_admin),
    INDEX idx_type (type),
    INDEX idx_date_generation (date_generation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TRIGGERS pour maintenir la cohérence
-- =====================================================

-- Trigger pour incrémenter nombre_documents dans categorie
DELIMITER //
CREATE TRIGGER after_document_insert
AFTER INSERT ON document
FOR EACH ROW
BEGIN
    IF NEW.id_categorie IS NOT NULL THEN
        UPDATE categorie SET nombre_documents = nombre_documents + 1 WHERE id = NEW.id_categorie;
    END IF;
END//
DELIMITER ;

-- Trigger pour décrémenter nombre_documents dans categorie
DELIMITER //
CREATE TRIGGER after_document_delete
AFTER DELETE ON document
FOR EACH ROW
BEGIN
    IF OLD.id_categorie IS NOT NULL THEN
        UPDATE categorie SET nombre_documents = nombre_documents - 1 WHERE id = OLD.id_categorie;
    END IF;
END//
DELIMITER ;

-- Trigger pour mettre à jour nombre_documents lors du changement de catégorie
DELIMITER //
CREATE TRIGGER after_document_update
AFTER UPDATE ON document
FOR EACH ROW
BEGIN
    IF OLD.id_categorie != NEW.id_categorie THEN
        IF OLD.id_categorie IS NOT NULL THEN
            UPDATE categorie SET nombre_documents = nombre_documents - 1 WHERE id = OLD.id_categorie;
        END IF;
        IF NEW.id_categorie IS NOT NULL THEN
            UPDATE categorie SET nombre_documents = nombre_documents + 1 WHERE id = NEW.id_categorie;
        END IF;
    END IF;
END//
DELIMITER ;

-- =====================================================
-- VUES UTILES
-- =====================================================

-- Vue pour les documents avec leurs détails complets
CREATE VIEW vue_documents_complets AS
SELECT 
    d.id,
    d.titre,
    d.auteur,
    d.genre,
    d.format,
    d.date_publication,
    d.resume,
    d.mots_cles,
    d.prix_par_jour,
    d.disponible,
    d.nombre_consultations,
    d.nombre_emprunts,
    d.note_globale,
    d.id_categorie,
    c.nom AS nom_categorie,
    dr.emplacement,
    dr.condition_livre,
    dr.isbn,
    dn.url,
    dn.chemin_fichier,
    dn.taille,
    dn.format_fichier,
    dn.hash_fichier,
    dn.telechargeable,
    CASE 
        WHEN dr.id_document IS NOT NULL THEN 'REEL'
        WHEN dn.id_document IS NOT NULL THEN 'NUMERIQUE'
        ELSE 'INCONNU'
    END AS type_document
FROM document d
LEFT JOIN categorie c ON d.id_categorie = c.id
LEFT JOIN document_reel dr ON d.id = dr.id_document
LEFT JOIN document_numerique dn ON d.id = dn.id_document;

-- Vue pour les emprunts en cours avec détails
CREATE VIEW vue_emprunts_en_cours AS
SELECT 
    e.id,
    e.date_emprunt,
    e.date_retour,
    e.etat,
    e.duree_max,
    e.penalite,
    u.id_personne AS id_utilisateur,
    p.nom AS nom_utilisateur,
    p.email AS email_utilisateur,
    d.id AS id_document,
    d.titre AS titre_document,
    d.auteur AS auteur_document,
    DATEDIFF(CURDATE(), e.date_retour) AS jours_retard
FROM emprunt e
INNER JOIN utilisateur u ON e.id_utilisateur = u.id_personne
INNER JOIN personne p ON u.id_personne = p.id
INNER JOIN document d ON e.id_document = d.id
WHERE e.etat = 'EN_COURS';

