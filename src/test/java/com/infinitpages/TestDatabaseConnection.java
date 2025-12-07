package com.infinitpages;

import com.infinitpages.util.db.DatabaseConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour vérifier la connexion à la base de données.
 */
public class TestDatabaseConnection {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("🔧 Initialisation des tests...");
    }
    
    @AfterAll
    public static void tearDown() {
        System.out.println("🧹 Nettoyage après les tests...");
        DatabaseConnection.close();
    }
    
    @Test
    @DisplayName("Test de l'initialisation du pool de connexions")
    public void testInitializeConnection() {
        System.out.println("\n📡 Test 1: Initialisation du pool de connexions");
        
        try {
            DatabaseConnection.initialize();
            assertTrue(DatabaseConnection.isInitialized(), 
                "Le pool de connexions devrait être initialisé");
            System.out.println("✅ Pool de connexions initialisé avec succès");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("database.properties file not found")) {
                System.err.println("❌ ERREUR : Fichier database.properties introuvable");
                System.err.println("   Le fichier doit être dans : src/main/resources/database.properties");
                System.err.println("   Exécutez : mvn clean compile pour copier le fichier");
            }
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            throw new AssertionError("L'initialisation a échoué", e);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("L'initialisation a échoué", e);
        }
    }
    
    @Test
    @DisplayName("Test de la connexion à la base de données")
    public void testConnection() {
        System.out.println("\n🔌 Test 2: Test de connexion à la base de données");
        
        try {
            if (!DatabaseConnection.isInitialized()) {
                DatabaseConnection.initialize();
            }
            
            boolean connected = DatabaseConnection.testConnection();
            assertTrue(connected, 
                "La connexion à la base de données devrait réussir");
            System.out.println("✅ Connexion à la base de données réussie");
            
            // Afficher les statistiques
            DatabaseConnection.printPoolStats();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test de connexion: " + e.getMessage());
            throw new AssertionError("Le test de connexion a échoué", e);
        }
    }
    
    @Test
    @DisplayName("Test d'obtention d'une connexion")
    public void testGetConnection() {
        System.out.println("\n🔗 Test 3: Obtention d'une connexion depuis le pool");
        
        try {
            if (!DatabaseConnection.isInitialized()) {
                DatabaseConnection.initialize();
            }
            
            java.sql.Connection conn = DatabaseConnection.getConnection();
            assertNotNull(conn, "La connexion ne devrait pas être null");
            assertFalse(conn.isClosed(), "La connexion ne devrait pas être fermée");
            
            // Tester une requête simple
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT 1");
            assertTrue(rs.next(), "La requête devrait retourner un résultat");
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("✅ Connexion obtenue et testée avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'obtention de la connexion: " + e.getMessage());
            throw new AssertionError("L'obtention de la connexion a échoué", e);
        }
    }
}

