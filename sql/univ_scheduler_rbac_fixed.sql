-- ============================================================
--  UNIV-SCHEDULER - Script SQL MySQL avec RBAC (corrige)
--  Licence 2 Informatique - POO Java - 2026
-- ============================================================

CREATE DATABASE IF NOT EXISTS univ_scheduler
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE univ_scheduler;

-- Pour relancer le script facilement
DROP VIEW IF EXISTS vue_taux_occupation;
DROP VIEW IF EXISTS vue_permissions_utilisateur;
DROP VIEW IF EXISTS vue_emploi_du_temps;

DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS conflit;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS cours;
DROP TABLE IF EXISTS creneau;
DROP TABLE IF EXISTS emploi_du_temps;
DROP TABLE IF EXISTS equipement;
DROP TABLE IF EXISTS salle;
DROP TABLE IF EXISTS batiment;
DROP TABLE IF EXISTS etudiant;
DROP TABLE IF EXISTS enseignant;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS `role`;

-- ============================================================
--  MODULE RBAC : ROLES & PERMISSIONS
-- ============================================================

CREATE TABLE `role` (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE permission (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE role_permission (
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id)       REFERENCES `role`(id)      ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id)  ON DELETE CASCADE
);

-- ============================================================
--  MODULE 1 : UTILISATEURS
-- ============================================================

CREATE TABLE utilisateur (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(100)  NOT NULL,
    prenom       VARCHAR(100)  NOT NULL,
    email        VARCHAR(150)  NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255)  NOT NULL,
    role_id      INT           NOT NULL,
    actif        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES `role`(id)
);

CREATE TABLE enseignant (
    id           INT PRIMARY KEY,
    specialite   VARCHAR(100),
    departement  VARCHAR(100),
    FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE etudiant (
    id               INT PRIMARY KEY,
    numero_etudiant  VARCHAR(20) NOT NULL UNIQUE,
    classe           VARCHAR(50) NOT NULL,
    FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- ============================================================
--  MODULE 2 : INFRASTRUCTURE
-- ============================================================

CREATE TABLE batiment (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    localisation  VARCHAR(200),
    nombre_etages INT          NOT NULL DEFAULT 1
);

CREATE TABLE salle (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    numero      VARCHAR(20)  NOT NULL,
    capacite    INT          NOT NULL,
    type        ENUM('TD', 'TP', 'AMPHI', 'REUNION') NOT NULL,
    disponible  BOOLEAN      NOT NULL DEFAULT TRUE,
    batiment_id INT          NOT NULL,
    FOREIGN KEY (batiment_id) REFERENCES batiment(id) ON DELETE CASCADE,
    UNIQUE (batiment_id, numero)
);

CREATE TABLE equipement (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    fonctionnel BOOLEAN      NOT NULL DEFAULT TRUE,
    salle_id    INT          NOT NULL,
    FOREIGN KEY (salle_id) REFERENCES salle(id) ON DELETE CASCADE
);

-- ============================================================
--  MODULE 3 : COURS ET EMPLOI DU TEMPS
-- ============================================================

CREATE TABLE emploi_du_temps (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    semestre  VARCHAR(50) NOT NULL,
    annee     YEAR        NOT NULL
);

CREATE TABLE creneau (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    jour         ENUM('LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI') NOT NULL,
    heure_debut  TIME        NOT NULL,
    heure_fin    TIME        NOT NULL,
    date_seance  DATE,
    CHECK (heure_fin > heure_debut)
);

CREATE TABLE cours (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    matiere            VARCHAR(150) NOT NULL,
    groupe             VARCHAR(50)  NOT NULL,
    type               ENUM('CM', 'TD', 'TP', 'EXAMEN') NOT NULL,
    enseignant_id      INT          NOT NULL,
    salle_id           INT,
    creneau_id         INT          NOT NULL,
    emploi_du_temps_id INT          NOT NULL,
    FOREIGN KEY (enseignant_id)      REFERENCES enseignant(id),
    FOREIGN KEY (salle_id)           REFERENCES salle(id),
    FOREIGN KEY (creneau_id)         REFERENCES creneau(id),
    FOREIGN KEY (emploi_du_temps_id) REFERENCES emploi_du_temps(id) ON DELETE CASCADE
);

-- ============================================================
--  MODULE 4 : RESERVATIONS
-- ============================================================

CREATE TABLE reservation (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    demandeur_id  INT          NOT NULL,
    salle_id      INT          NOT NULL,
    creneau_id    INT          NOT NULL,
    motif         VARCHAR(255),
    statut        ENUM('EN_ATTENTE', 'VALIDEE', 'ANNULEE', 'EXPIREE') NOT NULL DEFAULT 'EN_ATTENTE',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (demandeur_id) REFERENCES utilisateur(id),
    FOREIGN KEY (salle_id)     REFERENCES salle(id),
    FOREIGN KEY (creneau_id)   REFERENCES creneau(id)
);

-- ============================================================
--  MODULE 5 : CONFLITS ET NOTIFICATIONS
-- ============================================================

CREATE TABLE conflit (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    type        ENUM('SALLE_OCCUPEE', 'ENSEIGNANT_INDISPONIBLE', 'CAPACITE_INSUFFISANTE') NOT NULL,
    cours1_id   INT          NOT NULL,
    cours2_id   INT,
    resolu      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cours1_id) REFERENCES cours(id) ON DELETE CASCADE,
    FOREIGN KEY (cours2_id) REFERENCES cours(id) ON DELETE SET NULL
);

CREATE TABLE notification (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    destinataire_id INT          NOT NULL,
    message         TEXT         NOT NULL,
    type            ENUM('CONFLIT', 'CHANGEMENT_SALLE', 'RAPPEL_RESERVATION', 'PROBLEME_TECHNIQUE') NOT NULL,
    lue             BOOLEAN      NOT NULL DEFAULT FALSE,
    date_envoi      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (destinataire_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- ============================================================
--  DONNEES RBAC : ROLES ET PERMISSIONS
-- ============================================================

INSERT INTO `role` (nom, description) VALUES
('ADMINISTRATEUR',  'Gestion globale du systeme'),
('GESTIONNAIRE',    'Planification des emplois du temps'),
('ENSEIGNANT',      'Consultation et reservation'),
('ETUDIANT',        'Consultation uniquement');

INSERT INTO permission (nom, description) VALUES
('GERER_UTILISATEURS',     'Ajouter, modifier, supprimer des utilisateurs'),
('CONFIGURER_BATIMENTS',   'Gerer les batiments et salles'),
('DEFINIR_EQUIPEMENTS',    'Gerer les equipements des salles'),
('CONSULTER_STATISTIQUES', 'Voir les statistiques et rapports'),
('CREER_COURS',            'Creer un nouveau cours'),
('MODIFIER_COURS',         'Modifier un cours existant'),
('SUPPRIMER_COURS',        'Supprimer un cours'),
('ASSIGNER_SALLE',         'Assigner une salle a un cours'),
('RESOUDRE_CONFLIT',       'Resoudre les conflits de planification'),
('GENERER_EDT',            'Generer l emploi du temps'),
('CONSULTER_EDT',          'Consulter l emploi du temps'),
('RESERVER_SALLE',         'Reserver une salle ponctuellement'),
('SIGNALER_PROBLEME',      'Signaler un probleme technique'),
('RECHERCHER_SALLE',       'Rechercher une salle libre');

-- Permissions Administrateur (toutes)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM `role` r
JOIN permission p
WHERE r.nom = 'ADMINISTRATEUR';

-- Permissions Gestionnaire
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM `role` r
JOIN permission p
WHERE r.nom = 'GESTIONNAIRE'
  AND p.nom IN ('CREER_COURS', 'MODIFIER_COURS', 'SUPPRIMER_COURS',
                'ASSIGNER_SALLE', 'RESOUDRE_CONFLIT', 'GENERER_EDT',
                'CONSULTER_EDT', 'CONSULTER_STATISTIQUES');

-- Permissions Enseignant
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM `role` r
JOIN permission p
WHERE r.nom = 'ENSEIGNANT'
  AND p.nom IN ('CONSULTER_EDT', 'RESERVER_SALLE', 'SIGNALER_PROBLEME');

-- Permissions Etudiant
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM `role` r
JOIN permission p
WHERE r.nom = 'ETUDIANT'
  AND p.nom IN ('CONSULTER_EDT', 'RECHERCHER_SALLE');

-- ============================================================
--  DONNEES DE TEST
--  NOTE: ces mots de passe sont des valeurs de demo.
-- ============================================================

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role_id) VALUES
('Dupont',  'Alice',    'admin@univ.sn',         'admin123',         (SELECT id FROM `role` WHERE nom='ADMINISTRATEUR')),
('Martin',  'Bernard',  'gestionnaire@univ.sn',  'gestionnaire123',  (SELECT id FROM `role` WHERE nom='GESTIONNAIRE')),
('Diallo',  'Oumar',    'diallo@univ.sn',        'enseignant123',    (SELECT id FROM `role` WHERE nom='ENSEIGNANT')),
('Ndiaye',  'Fatou',    'ndiaye@univ.sn',        'enseignant123',    (SELECT id FROM `role` WHERE nom='ENSEIGNANT')),
('Ba',      'Ibrahima', 'ba@univ.sn',            'etudiant123',      (SELECT id FROM `role` WHERE nom='ETUDIANT'));

INSERT INTO enseignant (id, specialite, departement) VALUES
((SELECT id FROM utilisateur WHERE email='diallo@univ.sn'), 'Programmation Java', 'Informatique'),
((SELECT id FROM utilisateur WHERE email='ndiaye@univ.sn'), 'Bases de Donnees',   'Informatique');

INSERT INTO etudiant (id, numero_etudiant, classe) VALUES
((SELECT id FROM utilisateur WHERE email='ba@univ.sn'), 'ETU2026001', 'L2 Informatique');

INSERT INTO batiment (nom, localisation, nombre_etages) VALUES
('Batiment A', 'Campus principal', 3),
('Batiment B', 'Campus principal', 2);

INSERT INTO salle (numero, capacite, type, batiment_id) VALUES
('A101', 30,  'TD',    1),
('A102', 30,  'TD',    1),
('A201', 60,  'TP',    1),
('B001', 200, 'AMPHI', 2);

INSERT INTO equipement (nom, description, salle_id) VALUES
('Videoprojecteur',    'Epson EB-X41',   1),
('Tableau interactif', 'Smart Board',    2),
('Videoprojecteur',    'Epson EB-X41',   3),
('Climatisation',      'Split Daikin',   4);

INSERT INTO emploi_du_temps (semestre, annee) VALUES
('Semestre 2', 2026);

INSERT INTO creneau (jour, heure_debut, heure_fin) VALUES
('LUNDI',    '08:00', '10:00'),
('LUNDI',    '10:00', '12:00'),
('MARDI',    '08:00', '10:00'),
('MERCREDI', '14:00', '16:00');

INSERT INTO cours (matiere, groupe, type, enseignant_id, salle_id, creneau_id, emploi_du_temps_id) VALUES
('POO Java',         'G1', 'TD', (SELECT id FROM utilisateur WHERE email='diallo@univ.sn'), 1, 1, 1),
('Bases de Donnees', 'G1', 'CM', (SELECT id FROM utilisateur WHERE email='ndiaye@univ.sn'), 4, 2, 1),
('POO Java',         'G2', 'TD', (SELECT id FROM utilisateur WHERE email='diallo@univ.sn'), 2, 3, 1);

-- ============================================================
--  VUES UTILES
-- ============================================================

CREATE VIEW vue_emploi_du_temps AS
SELECT
    c.id AS cours_id,
    c.matiere,
    c.groupe,
    c.type AS type_cours,
    CONCAT(u.prenom, ' ', u.nom) AS enseignant,
    s.numero AS salle,
    s.capacite,
    cr.jour,
    cr.heure_debut,
    cr.heure_fin,
    edt.semestre
FROM cours c
JOIN utilisateur u       ON c.enseignant_id = u.id
LEFT JOIN salle s        ON c.salle_id = s.id
JOIN creneau cr          ON c.creneau_id = cr.id
JOIN emploi_du_temps edt ON c.emploi_du_temps_id = edt.id;

CREATE VIEW vue_permissions_utilisateur AS
SELECT
    u.id AS utilisateur_id,
    CONCAT(u.prenom, ' ', u.nom) AS nom_complet,
    u.email,
    r.nom AS role,
    p.nom AS permission
FROM utilisateur u
JOIN `role` r            ON u.role_id = r.id
JOIN role_permission rp  ON r.id = rp.role_id
JOIN permission p        ON rp.permission_id = p.id;

CREATE VIEW vue_taux_occupation AS
SELECT
    s.numero AS salle,
    s.capacite,
    COUNT(c.id) AS nombre_cours,
    ROUND(COUNT(c.id) * 100.0 / 20, 1) AS taux_occupation_pct
FROM salle s
LEFT JOIN cours c ON s.id = c.salle_id
GROUP BY s.id, s.numero, s.capacite;
