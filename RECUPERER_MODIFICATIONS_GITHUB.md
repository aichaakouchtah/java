# 📥 Récupérer les Modifications depuis GitHub

Ce guide vous explique comment récupérer les modifications faites par votre collègue sur GitHub.

---

## 🔍 Étape 1 : Vérifier l'état actuel

### Option A : Le projet est déjà un dépôt Git

Si votre projet est déjà connecté à GitHub, suivez ces étapes :

```bash
# 1. Vérifier l'état actuel
git status

# 2. Sauvegarder vos modifications locales (si nécessaire)
git add .
git commit -m "Mes modifications locales avant pull"

# 3. Récupérer les modifications depuis GitHub
git pull origin main
# ou
git pull origin master
```

### Option B : Le projet n'est pas encore un dépôt Git

Si votre projet n'est pas encore connecté à GitHub :

```bash
# 1. Initialiser Git (si pas déjà fait)
git init

# 2. Ajouter le remote GitHub
git remote add origin https://github.com/VOTRE_USERNAME/InfinitePages.git
# Remplacez VOTRE_USERNAME par votre nom d'utilisateur GitHub

# 3. Récupérer les modifications
git pull origin main --allow-unrelated-histories
```

---

## 🚀 Méthode Recommandée : Pull depuis GitHub

### Scénario 1 : Vous avez des modifications locales non commitées

```bash
# 1. Sauvegarder vos modifications
git stash

# 2. Récupérer les modifications depuis GitHub
git pull origin main

# 3. Réappliquer vos modifications
git stash pop

# 4. Résoudre les conflits si nécessaire (voir section Conflits)
```

### Scénario 2 : Vous n'avez pas de modifications locales

```bash
# Récupérer directement
git pull origin main
```

### Scénario 3 : Vous voulez voir les différences avant de pull

```bash
# 1. Voir ce qui va changer
git fetch origin
git log HEAD..origin/main --oneline

# 2. Voir les différences
git diff HEAD origin/main

# 3. Si tout est OK, faire le pull
git pull origin main
```

---

## 🔧 Commandes Utiles

### Vérifier la branche actuelle
```bash
git branch
```

### Voir les remotes configurés
```bash
git remote -v
```

### Changer de branche
```bash
git checkout main
# ou
git checkout master
```

### Voir l'historique des commits
```bash
git log --oneline --graph --all
```

---

## ⚠️ Résoudre les Conflits

Si vous avez des conflits après `git pull` :

### 1. Voir les fichiers en conflit
```bash
git status
```

### 2. Ouvrir les fichiers en conflit
Les conflits sont marqués comme ceci :
```
<<<<<<< HEAD
Votre code local
=======
Code de votre collègue
>>>>>>> origin/main
```

### 3. Résoudre manuellement
- Gardez votre code
- Gardez le code de votre collègue
- Combinez les deux

### 4. Marquer comme résolu
```bash
# Après avoir résolu les conflits dans un fichier
git add nom_du_fichier.java

# Quand tous les conflits sont résolus
git commit -m "Résolution des conflits"
```

---

## 📋 Checklist Complète

- [ ] Vérifier que Git est installé : `git --version`
- [ ] Vérifier l'état : `git status`
- [ ] Sauvegarder vos modifications : `git add .` puis `git commit`
- [ ] Vérifier le remote : `git remote -v`
- [ ] Récupérer les modifications : `git pull origin main`
- [ ] Résoudre les conflits si nécessaire
- [ ] Tester que tout fonctionne après le pull

---

## 🆘 Problèmes Courants

### Erreur : "fatal: not a git repository"
**Solution :**
```bash
git init
git remote add origin https://github.com/VOTRE_USERNAME/InfinitePages.git
```

### Erreur : "Updates were rejected"
**Solution :**
```bash
# Sauvegarder vos modifications
git stash

# Récupérer les modifications
git pull origin main

# Réappliquer vos modifications
git stash pop
```

### Erreur : "refusing to merge unrelated histories"
**Solution :**
```bash
git pull origin main --allow-unrelated-histories
```

### Erreur : "remote origin already exists"
**Solution :**
```bash
# Voir le remote actuel
git remote -v

# Si c'est le mauvais URL, le modifier
git remote set-url origin https://github.com/VOTRE_USERNAME/InfinitePages.git
```

---

## 💡 Astuce : Utiliser VS Code / IntelliJ

Si vous utilisez un IDE moderne :

1. **VS Code** : 
   - Ouvrez le panneau Source Control (Ctrl+Shift+G)
   - Cliquez sur "..." → "Pull"

2. **IntelliJ IDEA** :
   - VCS → Git → Pull
   - Ou utilisez le raccourci : Ctrl+T

---

## 📝 Résumé Rapide

```bash
# La commande principale
git pull origin main

# Si vous avez des modifications locales
git stash
git pull origin main
git stash pop
```

**Besoin d'aide ?** Dites-moi quelle erreur vous obtenez ! 🚀

