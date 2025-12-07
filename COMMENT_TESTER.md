# 🧪 Comment Tester le Projet InfinitePages

Guide rapide pour tester votre projet étape par étape.

---

## 📋 Prérequis

Avant de commencer, assurez-vous que :

- ✅ **Java 17** est installé
- ✅ **Maven** est installé (ou utilisez Maven Wrapper)
- ✅ **MySQL/XAMPP** est démarré
- ✅ **Base de données** `infinitpages` créée
- ✅ **Script SQL** `schema.sql` exécuté

---

## 🚀 Méthode 1 : Test Rapide (Recommandé)

### Étape 1 : Vérifier la configuration

Vérifiez `src/main/resources/database.properties` :
```properties
db.host=localhost
db.port=3306
db.name=infinitpages
db.username=root
db.password=votre_mot_de_passe
```

### Étape 2 : Créer la base de données

1. Ouvrez **phpMyAdmin** : http://localhost/phpmyadmin
2. Créez la base : `CREATE DATABASE infinitpages;`
3. Sélectionnez la base `infinitpages`
4. Onglet **SQL** → Copiez-collez le contenu de `src/main/resources/database/schema.sql`
5. Cliquez sur **Exécuter**

### Étape 3 : Test simple

Exécutez la classe de test simple :

```bash
# Depuis le terminal dans le dossier du projet
mvn compile exec:java -Dexec.mainClass="com.infinitpages.TestSimple"
```

Ou depuis votre IDE :
- Clic droit sur `TestSimple.java` → Run

**Résultat attendu :**
```
✅ Pool de connexions initialisé
✅ Connexion à la base de données réussie !
✅ 15 tables trouvées - OK
✅ CategorieDAO.findAll() fonctionne
```

---

## 🧪 Méthode 2 : Tests Unitaires avec JUnit

### Étape 1 : Exécuter les tests

```bash
# Compiler le projet
mvn compile

# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=TestDatabaseConnection
```

### Étape 2 : Vérifier les résultats

Les tests devraient afficher :
- ✅ Test de l'initialisation du pool
- ✅ Test de la connexion
- ✅ Test d'obtention d'une connexion
- ✅ Tests CRUD des DAO

---

## 🎯 Méthode 3 : Test de l'Application JavaFX

### Étape 1 : Lancer l'application

```bash
mvn javafx:run
```

Ou depuis votre IDE :
- Clic droit sur `Main.java` → Run

### Étape 2 : Vérifier

Une fenêtre JavaFX devrait s'ouvrir (même si elle est vide pour l'instant).

---

## 🔍 Tests Disponibles

### 1. TestSimple.java
**Usage :** Test rapide de la connexion et des tables
```bash
mvn compile exec:java -Dexec.mainClass="com.infinitpages.TestSimple"
```

### 2. TestDatabaseConnection.java
**Usage :** Tests unitaires de la connexion
```bash
mvn test -Dtest=TestDatabaseConnection
```

### 3. TestDAO.java
**Usage :** Tests CRUD complets des DAO
```bash
mvn test -Dtest=TestDAO
```

---

## ⚠️ Problèmes Courants et Solutions

### ❌ Erreur : "Connection refused"
**Solution :**
- Vérifiez que MySQL/XAMPP est démarré
- Vérifiez le port dans `database.properties` (3306)

### ❌ Erreur : "Access denied"
**Solution :**
- Vérifiez le nom d'utilisateur et mot de passe dans `database.properties`
- Testez la connexion dans phpMyAdmin

### ❌ Erreur : "Unknown database 'infinitpages'"
**Solution :**
```sql
CREATE DATABASE infinitpages CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### ❌ Erreur : "Table 'personne' doesn't exist"
**Solution :**
- Exécutez le script `schema.sql` dans phpMyAdmin
- Vérifiez que toutes les 15 tables sont créées

### ❌ Erreur : "ClassNotFoundException"
**Solution :**
```bash
mvn clean compile
```

---

## 📊 Checklist de Test

Avant de considérer que tout fonctionne :

- [ ] MySQL/XAMPP est démarré
- [ ] Base de données `infinitpages` créée
- [ ] Script `schema.sql` exécuté (15 tables créées)
- [ ] `database.properties` configuré correctement
- [ ] `TestSimple` s'exécute sans erreur
- [ ] `TestDatabaseConnection` passe tous les tests
- [ ] `TestDAO` passe tous les tests
- [ ] Application JavaFX démarre

---

## 🎓 Exemple de Test Manuel

### Test 1 : Créer un utilisateur

```java
// Dans votre code de test
UtilisateurDAO dao = new UtilisateurDAOImpl();
Utilisateur user = new Utilisateur();
user.setNom("Test");
user.setEmail("test@test.com");
user.setMotDePasse("pass");
user.setTypeUtilisateur(TypeUtilisateur.ETUDIANT);

Utilisateur saved = dao.save(user);
System.out.println("Utilisateur créé avec ID: " + saved.getId());
```

### Test 2 : Rechercher un document

```java
DocumentDAO dao = new DocumentDAOImpl();
List<Document> docs = dao.findDisponibles();
System.out.println("Documents disponibles: " + docs.size());
```

---

## 📝 Notes Importantes

1. **Les tests modifient la base de données** : Ils créent et suppriment des données de test
2. **Utilisez une base de test** : Pour éviter de polluer votre base de production
3. **Vérifiez les logs** : Les erreurs sont affichées dans la console

---

*Guide créé pour le projet InfinitePages*

