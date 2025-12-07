package com.infinitpages;

import com.infinitpages.model.dao.CategorieDAO;
import com.infinitpages.model.dao.DocumentDAO;
import com.infinitpages.model.dao.EmpruntDAO;
import com.infinitpages.model.dao.PersonneDAO;
import com.infinitpages.model.dao.UtilisateurDAO;
import com.infinitpages.model.dao.impl.CategorieDAOImpl;
import com.infinitpages.model.dao.impl.DocumentDAOImpl;
import com.infinitpages.model.dao.impl.EmpruntDAOImpl;
import com.infinitpages.model.dao.impl.UtilisateurDAOImpl;
import com.infinitpages.model.entity.Categorie;
import com.infinitpages.model.entity.Document;
import com.infinitpages.model.entity.Personne;
import com.infinitpages.model.entity.Utilisateur;
import com.infinitpages.util.constants.Genre;
import com.infinitpages.util.constants.TypeUtilisateur;
import com.infinitpages.util.db.DatabaseConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Tests pour vérifier le fonctionnement des DAO.
 * 
 * ⚠️ IMPORTANT : Ces tests nécessitent que :
 * 1. MySQL soit démarré
 * 2. La base de données 'infinitpages' existe
 * 3. Le script schema.sql ait été exécuté
 */
public class TestDAO {
    
    private static UtilisateurDAO utilisateurDAO;
    private static CategorieDAO categorieDAO;
    private static DocumentDAO documentDAO;
    private static EmpruntDAO empruntDAO;
    
    @BeforeAll
    public static void setUp() {
        System.out.println("🔧 Initialisation des tests DAO...");
        
        // Initialiser la connexion
        try {
            DatabaseConnection.initialize();
            System.out.println("✅ Pool de connexions initialisé");
            
            if (!DatabaseConnection.testConnection()) {
                System.err.println("⚠️  ATTENTION : Impossible de se connecter à la base de données");
                System.err.println("   Les tests nécessitent que :");
                System.err.println("   1. MySQL/XAMPP soit démarré");
                System.err.println("   2. La base 'infinitpages' existe");
                System.err.println("   3. Le script schema.sql ait été exécuté");
                System.err.println("   4. Les identifiants dans database.properties soient corrects");
                throw new RuntimeException("Impossible de se connecter à la base de données");
            }
            System.out.println("✅ Connexion à la base de données réussie");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("database.properties file not found")) {
                System.err.println("❌ ERREUR : Fichier database.properties introuvable");
                System.err.println("   Le fichier doit être dans : src/main/resources/database.properties");
            }
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
        
        // Créer les instances des DAO
        utilisateurDAO = new UtilisateurDAOImpl();
        categorieDAO = new CategorieDAOImpl();
        documentDAO = new DocumentDAOImpl();
        empruntDAO = new EmpruntDAOImpl();
        
        System.out.println("✅ DAO initialisés");
    }
    
    @AfterAll
    public static void tearDown() {
        System.out.println("🧹 Nettoyage après les tests...");
        DatabaseConnection.close();
    }
    
    @Test
    @DisplayName("Test CRUD Utilisateur")
    public void testUtilisateurCRUD() {
        System.out.println("\n👤 Test 1: CRUD Utilisateur");
        
        try {
            // CREATE
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom("Test User");
            utilisateur.setEmail("test@example.com");
            utilisateur.setMotDePasse("password123");
            utilisateur.setTypeUtilisateur(TypeUtilisateur.ETUDIANT);
            
            Utilisateur saved = utilisateurDAO.save(utilisateur);
            assertNotNull(saved, "L'utilisateur devrait être sauvegardé");
            assertTrue(saved.getId() > 0, "L'utilisateur devrait avoir un ID");
            System.out.println("✅ Utilisateur créé avec ID: " + saved.getId());
            
            // READ
            Optional<Personne> found = utilisateurDAO.findById(saved.getId());
            assertTrue(found.isPresent(), "L'utilisateur devrait être trouvé");
            System.out.println("✅ Utilisateur trouvé par ID");
            
            // UPDATE
            saved.setNom("Test User Modifié");
            boolean updated = utilisateurDAO.update(saved);
            assertTrue(updated, "L'utilisateur devrait être mis à jour");
            System.out.println("✅ Utilisateur mis à jour");
            
            // DELETE
            boolean deleted = utilisateurDAO.delete(saved.getId());
            assertTrue(deleted, "L'utilisateur devrait être supprimé");
            System.out.println("✅ Utilisateur supprimé");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test CRUD Utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("Le test CRUD Utilisateur a échoué", e);
        }
    }
    
    @Test
    @DisplayName("Test CRUD Categorie")
    public void testCategorieCRUD() {
        System.out.println("\n📚 Test 2: CRUD Categorie");
        
        try {
            // CREATE
            Categorie categorie = new Categorie();
            categorie.setNom("Informatique");
            categorie.setDescription("Livres sur l'informatique");
            
            Categorie saved = categorieDAO.save(categorie);
            assertNotNull(saved, "La catégorie devrait être sauvegardée");
            assertTrue(saved.getId() > 0, "La catégorie devrait avoir un ID");
            System.out.println("✅ Catégorie créée avec ID: " + saved.getId());
            
            // READ
            Optional<Categorie> found = categorieDAO.findById(saved.getId());
            assertTrue(found.isPresent(), "La catégorie devrait être trouvée");
            System.out.println("✅ Catégorie trouvée par ID");
            
            // FIND BY NAME
            Optional<Categorie> foundByName = categorieDAO.findByNom("Informatique");
            assertTrue(foundByName.isPresent(), "La catégorie devrait être trouvée par nom");
            System.out.println("✅ Catégorie trouvée par nom");
            
            // DELETE
            boolean deleted = categorieDAO.delete(saved.getId());
            assertTrue(deleted, "La catégorie devrait être supprimée");
            System.out.println("✅ Catégorie supprimée");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test CRUD Categorie: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("Le test CRUD Categorie a échoué", e);
        }
    }
    
    @Test
    @DisplayName("Test CRUD Document")
    public void testDocumentCRUD() {
        System.out.println("\n📖 Test 3: CRUD Document");
        
        try {
            // Créer une catégorie d'abord
            Categorie categorie = new Categorie();
            categorie.setNom("Test Category");
            categorie.setDescription("Catégorie de test");
            categorie = categorieDAO.save(categorie);
            
            // CREATE
            Document document = new Document() {
                // Classe anonyme pour instancier Document abstraite
            };
            document.setTitre("Test Document");
            document.setAuteur("Test Author");
            document.setGenre(Genre.LIVRE_SCIENTIFIQUE);
            document.setFormat("Livre");
            document.setDatePublication(LocalDate.now());
            document.setResume("Un livre de test");
            document.setPrixParJour(2.50);
            document.setDisponible(true);
            document.setCategorieEntity(categorie);
            
            Document saved = documentDAO.save(document);
            assertNotNull(saved, "Le document devrait être sauvegardé");
            assertTrue(saved.getId() > 0, "Le document devrait avoir un ID");
            System.out.println("✅ Document créé avec ID: " + saved.getId());
            
            // READ
            Optional<Document> found = documentDAO.findById(saved.getId());
            assertTrue(found.isPresent(), "Le document devrait être trouvé");
            System.out.println("✅ Document trouvé par ID");
            
            // SEARCH
            var results = documentDAO.findByTitre("Test");
            assertFalse(results.isEmpty(), "La recherche devrait retourner des résultats");
            System.out.println("✅ Recherche de document fonctionne");
            
            // DELETE
            boolean deleted = documentDAO.delete(saved.getId());
            assertTrue(deleted, "Le document devrait être supprimé");
            System.out.println("✅ Document supprimé");
            
            // Nettoyer la catégorie
            categorieDAO.delete(categorie.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test CRUD Document: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("Le test CRUD Document a échoué", e);
        }
    }
    
    @Test
    @DisplayName("Test de recherche Utilisateur par email")
    public void testFindUtilisateurByEmail() {
        System.out.println("\n🔍 Test 4: Recherche Utilisateur par email");
        
        try {
            // Créer un utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom("Email Test");
            utilisateur.setEmail("emailtest@example.com");
            utilisateur.setMotDePasse("password");
            utilisateur.setTypeUtilisateur(TypeUtilisateur.ETUDIANT);
            
            Utilisateur saved = utilisateurDAO.save(utilisateur);
            
            // Rechercher par email
            Optional<Personne> found = utilisateurDAO.findByEmail("emailtest@example.com");
            assertTrue(found.isPresent(), "L'utilisateur devrait être trouvé par email");
            System.out.println("✅ Utilisateur trouvé par email");
            
            // Nettoyer
            utilisateurDAO.delete(saved.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test de recherche: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("Le test de recherche a échoué", e);
        }
    }
}

