# Cotizia - Gestion de Cotisation

Ce projet est une application JavaFX pour la gestion de tontines (cotisations).

## Prérequis
1. **Java JDK 19+**
2. **MySQL Server**
3. **Maven** (Inclus dans le dossier `Maven_Install` ou utilisez votre installation locale)

## Installation et Configuration

### 1. Configuration de la Base de Données
1. Lancez votre serveur MySQL (par exemple via XAMPP ou MySQL Workbench).
2. Exécutez le script SQL `database_setup.sql` situé à la racine du projet pour créer la base `cotizia_db` et ses tables.
3. Si votre utilisateur MySQL n'est pas `root` ou possède un mot de passe, modifiez les constantes `USER` et `PASSWORD` dans le fichier :
   `src/main/java/com/cotizia/cotizia/utils/DBConnection.java`

### 2. Importation dans NetBeans
1. Ouvrez NetBeans IDE.
2. Allez dans `File` -> `Open Project`.
3. Naviguez vers le dossier du projet et sélectionnez-le (NetBeans reconnaîtra l'icône Maven).
4. Attendez le chargement des dépendances.

### 3. Exécution
- Cliquez avec le bouton droit sur le projet -> `Run`.
- Ou utilisez la ligne de commande : `mvn javafx:run`

## Identifiants par défaut
- **Administrateur** : admin@cotizia.com / admin
