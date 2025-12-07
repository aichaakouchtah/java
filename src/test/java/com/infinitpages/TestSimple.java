package com.infinitpages;

import com.infinitpages.util.db.DatabaseConnection;

/**
 * Test simple pour vérifier rapidement que tout fonctionne.
 * Exécutez cette classe directement depuis votre IDE ou avec :
 * mvn compile exec:java -Dexec.mainClass="com.infinitpages.TestSimple"
 */
public class TestSimple {
    
    public static void main(String[] args) {
        System.out.println("🧪 Test Simple - InfinitePages");
        System.out.println("==============================\n");
        
        // Test 1: Connexion à la base de données
        System.out.println("📡 Test 1: Connexion à la base de données");
        try {
            DatabaseConnection.initialize();
            System.out.println("✅ Pool de connexions initialisé");
            
            if (DatabaseConnection.testConnection()) {
                System.out.println("✅ Connexion à la base de données réussie !");
                DatabaseConnection.printPoolStats();
            } else {
                System.out.println("❌ Échec de la connexion");
                System.out.println("Vérifiez :");
                System.out.println("  - MySQL est démarré");
                System.out.println("  - La base 'infinitpages' existe");
                System.out.println("  - Les identifiants dans database.properties sont corrects");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n💡 Solutions possibles :");
            System.out.println("  1. Vérifiez que MySQL/XAMPP est démarré");
            System.out.println("  2. Créez la base de données : CREATE DATABASE infinitpages;");
            System.out.println("  3. Exécutez le script schema.sql");
            System.out.println("  4. Vérifiez database.properties");
            return;
        }
        
        // Test 2: Vérifier que les tables existent
        System.out.println("\n📊 Test 2: Vérification des tables");
        try {
            var conn = DatabaseConnection.getConnection();
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SHOW TABLES");
            
            int tableCount = 0;
            System.out.println("Tables trouvées :");
            while (rs.next()) {
                tableCount++;
                System.out.println("  - " + rs.getString(1));
            }
            
            if (tableCount == 0) {
                System.out.println("⚠️  Aucune table trouvée !");
                System.out.println("   Exécutez le script schema.sql dans MySQL");
            } else if (tableCount < 15) {
                System.out.println("⚠️  Seulement " + tableCount + " tables trouvées (attendu: 15)");
                System.out.println("   Vérifiez que schema.sql a été exécuté complètement");
            } else {
                System.out.println("✅ " + tableCount + " tables trouvées - OK");
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la vérification des tables: " + e.getMessage());
        }
        
        // Test 3: Test simple d'un DAO
        System.out.println("\n🔧 Test 3: Test d'un DAO");
        try {
            var categorieDAO = new com.infinitpages.model.dao.impl.CategorieDAOImpl();
            var categories = categorieDAO.findAll();
            System.out.println("✅ CategorieDAO.findAll() fonctionne - " + categories.size() + " catégories");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test DAO: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fermeture
        System.out.println("\n✅ Tests terminés");
        DatabaseConnection.close();
    }
}

