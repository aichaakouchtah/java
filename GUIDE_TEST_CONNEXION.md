# Guide de Test de Connexion - InfinitPages

Ce guide explique comment tester la connexion à la base de données et les DAO.

## 📋 Prérequis

1. **MySQL installé et démarré**
   - Vérifiez que MySQL est en cours d'exécution
   - Port par défaut : 3306

2. **Base de données créée**
   ```sql
   CREATE DATABASE infinitpages;
   ```

3. **Tables créées**
   - Exécutez le script `database_schema.sql` dans MySQL
   - Ou utilisez un outil comme MySQL Workbench, phpMyAdmin, etc.

---

## 🚀 Méthode 1 : Test Simple (Recommandé)

### Étape 1 : Configurer les paramètres

Ouvrez `TestConnexion.java` et modifiez les paramètres :

```java
String databaseName = "infinitpages";  // Nom de votre base
String username = "root";              // Votre utilisateur MySQL
String password = "votre_mot_de_passe"; // Votre mot de passe MySQL
```

### Étape 2 : Exécuter le test

**Dans votre IDE :**
1. Ouvrez `TestConnexion.java`
2. Clic droit → "Run" ou "Exécuter"

**En ligne de commande :**
```bash
mvn compile exec:java -Dexec.mainClass="com.infinitpages.TestConnexion"
```

### Étape 3 : Vérifier les résultats

Vous devriez voir :
```
=== Test de Connexion à la Base de Données ===

1. Test de connexion à la base de données...
   ✓ Connexion initialisée
   ✓ Connexion réussie !
   ✓ Statistiques du pool: ...

2. Test des opérations DAO...
   2.1. Création d'un Admin de test...
      ✓ Admin créé avec ID: 1
   ...
```

---

## 🛠️ Méthode 2 : Test depuis Main.java

Vous pouvez aussi tester directement dans `Main.java` :

```java
public static void main(String[] args) {
    try {
        // Initialiser la connexion
        DatabaseConnection.initializeDefault("infinitpages", "root", "password");
        
        // Tester la connexion
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Connexion réussie !");
        }
        
        // Tester un DAO
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = new Admin("Test", "test@test.fr", "mdp", "Dept", TypeAdmin.BOTH);
        adminDAO.save(admin);
        System.out.println("✓ Admin sauvegardé avec ID: " + admin.getId());
        
    } catch (Exception e) {
        System.out.println("✗ Erreur: " + e.getMessage());
    } finally {
        DatabaseConnection.close();
    }
}
```

---

## ⚠️ Dépannage

### Erreur : "Table doesn't exist"

**Solution :**
1. Exécutez le script `database_schema.sql` dans MySQL
2. Vérifiez que les tables sont créées :
   ```sql
   USE infinitpages;
   SHOW TABLES;
   ```

### Erreur : "Access denied"

**Solution :**
- Vérifiez votre nom d'utilisateur et mot de passe MySQL
- Assurez-vous que l'utilisateur a les droits sur la base de données

### Erreur : "Connection refused"

**Solution :**
- Vérifiez que MySQL est démarré
- Vérifiez le port (par défaut 3306)
- Vérifiez les paramètres de connexion

### Erreur : "Unknown database"

**Solution :**
- Créez la base de données :
  ```sql
  CREATE DATABASE infinitpages;
  ```

---

## 📊 Structure des Tables Requises

Les tables suivantes doivent exister :

1. **personnes** - Table principale
2. **admins** - Table pour les admins
3. **super_admins** - Table pour les super-admins

Voir `database_schema.sql` pour le script complet.

---

## ✅ Checklist de Test

- [ ] MySQL est démarré
- [ ] Base de données `infinitpages` créée
- [ ] Tables créées (exécution de `database_schema.sql`)
- [ ] Paramètres de connexion configurés dans `TestConnexion.java`
- [ ] Test exécuté avec succès
- [ ] Données de test créées dans la base

---

## 💡 Conseils

1. **Gardez les données de test** : Ne supprimez pas les données de test si vous voulez continuer à tester
2. **Vérifiez les logs** : Les logs SLF4J vous donneront plus de détails
3. **Testez étape par étape** : Testez d'abord la connexion, puis les DAO

---

## 🔍 Vérification Manuelle dans MySQL

Vous pouvez vérifier manuellement dans MySQL :

```sql
USE infinitpages;

-- Voir tous les admins
SELECT p.*, a.type_admin, a.departement 
FROM personnes p 
INNER JOIN admins a ON p.id = a.personne_id;

-- Voir tous les super-admins
SELECT p.*, a.type_admin, a.departement 
FROM personnes p 
INNER JOIN admins a ON p.id = a.personne_id
INNER JOIN super_admins sa ON p.id = sa.personne_id;
```


