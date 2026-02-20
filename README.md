# Cotizia - Système de Gestion de Tontine Moderne

Cotizia est une application JavaFX robuste conçue pour simplifier la gestion des cercles de cotisation (tontines). Elle offre une interface intuitive et des fonctionnalités avancées pour les administrateurs, les collecteurs et les participants.

## 🚀 Fonctionnalités Clés

- **Tableau de Bord Analytique** : Visualisation en temps réel de l'état des recouvrements et des tendances mensuelles via des graphiques (PieChart & BarChart).
- **Sécurité Avancée** : Hachage des mots de passe avec SHA-256 et système de migration automatique des anciens comptes.
- **Rapports PDF Professionnels** : Génération de rapports détaillés pour chaque cycle de cotisation.
- **Gestion Automatisée des Retards** : Détection et marquage automatique des paiements en retard.
- **Contrôle d'Accès par Rôle (RBAC)** : interfaces et actions adaptées aux profils ADMIN, COLLECTEUR et ADHERANT.
- **Mouchard (Audit Log)** : Suivi complet des actions administratives et financières pour une transparence totale.

## 🛠️ Stack Technique

- **Langage** : Java 19+
- **Interface** : JavaFX
- **Gestionnaire de dépendances** : Maven
- **Base de données** : MySQL
- **Bibliothèque PDF** : OpenPDF

## 📦 Installation et Configuration

Pour une installation détaillée pas à pas, veuillez consulter le fichier : 
👉 **[Instructions_Installation.md](Instructions_Installation.md)**

### Résumé rapide :
1. Importer le schéma de base de données : `database_setup.sql`.
2. Configurer les accès MySQL dans `src/main/java/com/cotizia/cotizia/utils/DBConnection.java`.
3. Compiler le projet avec Maven : `mvn clean install`.
4. Lancer l'application : `mvn javafx:run`.

## 🔒 Identifiants par défaut (pour test)

- **Admin** : `admin@cotizia.com` / `admin`
- **Collecteur** : `m.collecteur@example.com` / `admin123`
- **Adhérent** : `j.doe@example.com` / `password123`

---
*Développé avec ❤️ pour rendre la gestion des tontines plus transparente et efficace.*
