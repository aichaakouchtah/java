# 🧪 Guide de Test - Projet InfinitePages

Ce guide vous explique comment tester votre projet étape par étape.

---

## 📋 Prérequis

Avant de tester, assurez-vous que :

1. ✅ **Java 17** est installé
2. ✅ **MySQL** est installé et démarré
3. ✅ **XAMPP** (ou MySQL) est en cours d'exécution
4. ✅ La base de données `infinitpages` existe
5. ✅ Le script SQL `schema.sql` a été exécuté

---

## 🔧 Étape 1 : Vérifier la Configuration

### 1.1 Vérifier database.properties

Vérifiez que le fichier `src/main/resources/database.properties` contient les bonnes informations :

```properties
db.host=localhost
db.port=3306
db.name=infinitpages
db.username=root
db.password=votre_mot_de_passe
```

### 1.2 Vérifier que MySQL est démarré

```bash
# Vérifier que MySQL écoute sur le port 3306
netstat -an | findstr 3306
```

---

## 🗄️ Étape 2 : Créer la Base de Données

### 2.1 Créer la base de données

Ouvrez **phpMyAdmin** (http://localhost/phpmyadmin) ou utilisez MySQL en ligne de commande :

```sql
CREATE DATABASE IF NOT EXISTS infinitpages CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE infinitpages;
```

### 2.2 Exécuter le script SQL

1. Ouvrez phpMyAdmin
2. Sélectionnez la base `infinitpages`
3. Cliquez sur l'onglet **SQL**
4. Copiez le contenu de `src/main/resources/database/schema.sql`
5. Collez et cliquez sur **Exécuter**

### 2.3 Vérifier que les tables sont créées

```sql
SHOW TABLES;
```

Vous devriez voir 15 tables :
- personne, utilisateur, admin, superadmin
- categorie
- document, document_reel, document_numerique
- emprunt, paiement, historique, historique_document
- avis, notification, rapport

---

## 🧪 Étape 3 : Tester la Connexion à la Base de Données

### 3.1 Test simple avec Main.java

Modifiez temporairement `Main.java` pour tester la connexion :

```java
public static void main(String[] args) {
    // Test de connexion sans JavaFX
    try {
        DatabaseConnection.initialize();
        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ Connexion réussie !");
            DatabaseConnection.printPoolStats();
        } else {
            System.out.println("❌ Échec de la connexion");
        }
        DatabaseConnection.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 3.2 Exécuter le test

```bash
mvn compile exec:java -Dexec.mainClass="com.infinitpages.Main"
```

---

## 🧪 Étape 4 : Tester les DAO

### 4.1 Créer une classe de test simple

Créez `src/test/java/com/infinitpages/TestDAO.java` (voir fichier créé)

### 4.2 Exécuter les tests

```bash
# Compiler le projet
mvn compile

# Exécuter les tests
mvn test
```

---

## 🚀 Étape 5 : Tester l'Application JavaFX

### 5.1 Lancer l'application

```bash
mvn javafx:run
```

Ou depuis votre IDE :
- Clic droit sur `Main.java` → Run

### 5.2 Vérifier que l'application démarre

Une fenêtre JavaFX devrait s'ouvrir (même si elle est vide pour l'instant).

---

## 📝 Tests Recommandés

### Test 1 : Connexion à la Base de Données
- ✅ Vérifier que la connexion fonctionne
- ✅ Vérifier le pool HikariCP

### Test 2 : DAO - CRUD de base
- ✅ Créer un utilisateur
- ✅ Lire un utilisateur
- ✅ Mettre à jour un utilisateur
- ✅ Supprimer un utilisateur

### Test 3 : DAO - Relations
- ✅ Créer une catégorie
- ✅ Créer un document avec catégorie
- ✅ Créer un emprunt

### Test 4 : Services
- ✅ Tester LoanService.emprunterDocument()
- ✅ Tester LoanService.retournerDocument()

---

## ⚠️ Problèmes Courants

### Erreur : "Connection refused"
- Vérifiez que MySQL est démarré
- Vérifiez le port dans database.properties

### Erreur : "Access denied"
- Vérifiez le nom d'utilisateur et mot de passe
- Vérifiez les permissions MySQL

### Erreur : "Unknown database"
- Créez la base de données `infinitpages`
- Exécutez le script schema.sql

### Erreur : "Table doesn't exist"
- Exécutez le script schema.sql
- Vérifiez que toutes les tables sont créées

---

## 📊 Checklist de Test

- [ ] MySQL est démarré
- [ ] Base de données `infinitpages` créée
- [ ] Script `schema.sql` exécuté
- [ ] Configuration `database.properties` correcte
- [ ] Test de connexion réussi
- [ ] Test des DAO réussi
- [ ] Application JavaFX démarre

---

*Guide créé pour le projet InfinitePages*

