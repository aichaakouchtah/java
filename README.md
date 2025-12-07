<<<<<<< HEAD
# 📚 Bibliothèque Numérique - InfinitPages

Application de gestion de bibliothèque numérique développée en Java avec JavaFX, suivant l'architecture MVC.

## 🏗️ Architecture du Projet

Le projet suit une architecture **MVC (Model-View-Controller)** pour une séparation claire des responsabilités.

### Structure des Packages

```
com.infinitpages/
├── model/          # Modèle de données et logique métier
│   ├── entity/     # Entités métier (User, Document, Loan, etc.)
│   ├── dao/        # Data Access Objects (accès base de données)
│   └── service/    # Services métier (orchestration)
│
├── view/           # Interfaces utilisateur JavaFX
│   ├── auth/       # Authentification
│   ├── dashboard/  # Tableaux de bord
│   ├── documents/  # Gestion des documents
│   ├── loans/      # Gestion des emprunts
│   ├── admin/      # Interfaces administratives
│   └── common/     # Composants réutilisables
│
├── controller/     # Contrôleurs MVC
│   ├── auth/       # Contrôleurs d'authentification
│   ├── dashboard/  # Contrôleurs des dashboards
│   ├── documents/  # Contrôleurs de documents
│   ├── loans/      # Contrôleurs d'emprunts
│   └── admin/      # Contrôleurs administratifs
│
└── util/           # Utilitaires
    ├── db/         # Configuration base de données
    ├── config/     # Configuration application
    ├── exception/  # Exceptions personnalisées
    └── constants/  # Constantes
```

## 📋 Fonctionnalités

### Pour les Utilisateurs
- ✅ Recherche et consultation de documents
- ✅ Emprunt de documents
- ✅ Suivi des emprunts en cours
- ✅ Historique des emprunts
- ✅ Gestion du profil utilisateur

### Pour les Bibliothécaires
- ✅ Gestion des documents (ajout, modification, suppression)
- ✅ Gestion des catégories
- ✅ Gestion des utilisateurs
- ✅ Gestion des emprunts et retours
- ✅ Génération de rapports et statistiques

## 🎯 Rôles Utilisateurs

- **Étudiant** : Consultation et emprunt limité
- **Enseignant** : Consultation et emprunt étendu
- **Personnel académique** : Accès privilégié
- **Bibliothécaire** : Accès complet et administration

## 🛠️ Technologies

- **Java** : Langage de programmation
- **JavaFX** : Interface graphique
- **JDBC** : Accès base de données
- **HikariCP** : Pool de connexions
- **PostgreSQL** : Base de données relationnelle (recommandé)

## 📖 Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - Détails de l'architecture des packages
- [DATABASE_CHOICE.md](DATABASE_CHOICE.md) - Choix et configuration de la base de données

## 🚀 Prochaines Étapes

1. Configuration de la base de données
2. Création des entités (Model)
3. Implémentation des DAO
4. Développement des services
5. Création des interfaces JavaFX (View)
6. Implémentation des contrôleurs (Controller)

---

**Note** : Cette structure est prête pour le développement étape par étape.

=======
# java
>>>>>>> 7a002118f496b3a76a1ae8b230c9f1635db72c14
