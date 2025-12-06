package com.infinitpages;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale de l'application InfinitPages.
 * Point d'entrée de l'application JavaFX.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
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
     * Point d'entrée principal de l'application.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}

