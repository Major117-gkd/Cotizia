-- Creation de la base de donnees
CREATE DATABASE IF NOT EXISTS cotizia_db;
USE cotizia_db;

-- Table utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- ADMIN, COLLECTEUR, ADHERANT
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    matricule VARCHAR(50) -- Pour les collecteurs
) ENGINE=InnoDB;

-- Table cycle
CREATE TABLE IF NOT EXISTS cycle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    montant_cotisation DOUBLE NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    frequence VARCHAR(20) NOT NULL, -- HEBDOMADAIRE, MENSUEL
    etat VARCHAR(20) NOT NULL, -- EN_COURS, TERMINE
    collecteur_id INT NOT NULL,
    FOREIGN KEY (collecteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table participant
CREATE TABLE IF NOT EXISTS participant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cycle_id INT NOT NULL,
    utilisateur_id INT NOT NULL,
    date_inscription DATE NOT NULL,
    position_beneficiaire INT NOT NULL,
    FOREIGN KEY (cycle_id) REFERENCES cycle(id) ON DELETE CASCADE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table echeance (paiements prevus)
CREATE TABLE IF NOT EXISTS echeance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    participant_id INT NOT NULL,
    date_prevue DATE NOT NULL,
    date_paiement DATE,
    montant_paye DOUBLE DEFAULT 0,
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, PAYE, RETARD
    FOREIGN KEY (participant_id) REFERENCES participant(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table mouchard (logs d'activite)
CREATE TABLE IF NOT EXISTS mouchard (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action TEXT NOT NULL,
    date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utilisateur_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Insertion d'un compte administrateur par defaut
-- Email: admin@cotizia.com | Password: admin
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) 
VALUES ('System', 'Admin', 'admin@cotizia.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id;
