package com.infinitpages;

import com.infinitpages.util.db.DatabaseConnection;
import com.infinitpages.util.config.DatabaseConfig;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principale de l'application InfinitPages.
 * Point d'entrée de l'application JavaFX.
 */
public class Main extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    @Override
    public void start(Stage primaryStage) {
        // Initialiser la base de données au démarrage
        initializeDatabase();
        
        // TODO: Initialiser l'interface utilisateur
        // Pour l'instant, on affiche juste un message
        primaryStage.setTitle("InfinitPages - Bibliothèque Numérique");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        
        // TODO: Charger la vue de connexion ou le dashboard
        // LoginView loginView = new LoginView();
        // primaryStage.setScene(loginView.getScene());
        
        primaryStage.show();
    }
    
    /**
     * Initialise la connexion à la base de données.
     * Lit la configuration depuis database.properties.
     */
    private void initializeDatabase() {
        try {
            // Charger la configuration
            DatabaseConfig config = DatabaseConfig.getInstance();
            logger.info(config.getConfigSummary());
            
            // Initialiser la connexion avec la configuration
            DatabaseConnection.initialize();
            
            // Tester la connexion
            if (DatabaseConnection.testConnection()) {
                logger.info("✓ Connexion à la base de données réussie !");
            } else {
                logger.error("✗ Échec de la connexion à la base de données");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la base de données", e);
            logger.error("Vérifiez votre configuration dans src/main/resources/database.properties");
        }
    }
    
    /**
     * Point d'entrée principal de l'application.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Fermer proprement la connexion à la fermeture de l'application
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.close();
            logger.info("Connexion à la base de données fermée");
        }));
        
        launch(args);
    }
    
}

