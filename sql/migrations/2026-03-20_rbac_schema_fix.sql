-- ============================================================
-- UNIV-SCHEDULER - Migration de correction (base existante)
-- Date: 2026-03-20
-- Objectif:
-- 1) Corriger la FK cours.enseignant_id -> enseignant(id)
-- 2) Recreer les vues utiles
-- 3) Rendre l'affectation RBAC robuste (sans IDs fixes)
-- ============================================================

USE univ_scheduler;

-- ------------------------------------------------------------
-- 0) Pre-requis minimaux (idempotent)
-- ------------------------------------------------------------

INSERT IGNORE INTO `role` (nom, description) VALUES
('ADMINISTRATEUR',  'Gestion globale du systeme'),
('GESTIONNAIRE',    'Planification des emplois du temps'),
('ENSEIGNANT',      'Consultation et reservation'),
('ETUDIANT',        'Consultation uniquement');

INSERT IGNORE INTO permission (nom, description) VALUES
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

-- ------------------------------------------------------------
-- 1) FK cours.enseignant_id -> enseignant(id)
-- ------------------------------------------------------------
-- Cette procedure:
-- - stoppe la migration si des cours pointent vers un utilisateur non enseignant
-- - supprime la FK actuelle vers utilisateur (si presente)
-- - cree la FK vers enseignant (si absente)

DROP PROCEDURE IF EXISTS sp_fix_fk_cours_enseignant;

DELIMITER $$
CREATE PROCEDURE sp_fix_fk_cours_enseignant()
BEGIN
    DECLARE v_invalid_count INT DEFAULT 0;
    DECLARE v_fk_to_utilisateur VARCHAR(128) DEFAULT NULL;
    DECLARE v_fk_to_enseignant VARCHAR(128) DEFAULT NULL;

    -- Verifie que toutes les references actuelles existent bien dans enseignant
    SELECT COUNT(*)
      INTO v_invalid_count
      FROM cours c
 LEFT JOIN enseignant e ON e.id = c.enseignant_id
     WHERE e.id IS NULL;

    IF v_invalid_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Migration annulee: certains cours referencent un utilisateur qui n est pas dans enseignant.';
    END IF;

    -- Cherche la FK actuelle vers utilisateur
    SELECT kcu.CONSTRAINT_NAME
      INTO v_fk_to_utilisateur
      FROM information_schema.KEY_COLUMN_USAGE kcu
     WHERE kcu.TABLE_SCHEMA = DATABASE()
       AND kcu.TABLE_NAME = 'cours'
       AND kcu.COLUMN_NAME = 'enseignant_id'
       AND kcu.REFERENCED_TABLE_NAME = 'utilisateur'
     LIMIT 1;

    IF v_fk_to_utilisateur IS NOT NULL THEN
        SET @sql_drop_fk = CONCAT('ALTER TABLE cours DROP FOREIGN KEY `', v_fk_to_utilisateur, '`');
        PREPARE stmt_drop_fk FROM @sql_drop_fk;
        EXECUTE stmt_drop_fk;
        DEALLOCATE PREPARE stmt_drop_fk;
    END IF;

    -- Verifie si la FK vers enseignant existe deja
    SELECT kcu.CONSTRAINT_NAME
      INTO v_fk_to_enseignant
      FROM information_schema.KEY_COLUMN_USAGE kcu
     WHERE kcu.TABLE_SCHEMA = DATABASE()
       AND kcu.TABLE_NAME = 'cours'
       AND kcu.COLUMN_NAME = 'enseignant_id'
       AND kcu.REFERENCED_TABLE_NAME = 'enseignant'
     LIMIT 1;

    IF v_fk_to_enseignant IS NULL THEN
        ALTER TABLE cours
            ADD CONSTRAINT fk_cours_enseignant
            FOREIGN KEY (enseignant_id) REFERENCES enseignant(id);
    END IF;
END$$
DELIMITER ;

CALL sp_fix_fk_cours_enseignant();
DROP PROCEDURE IF EXISTS sp_fix_fk_cours_enseignant;

-- ------------------------------------------------------------
-- 2) RBAC role_permission (idempotent, sans IDs fixes)
-- ------------------------------------------------------------

-- Administrateur: toutes les permissions
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
  FROM `role` r
  JOIN permission p
 WHERE r.nom = 'ADMINISTRATEUR';

-- Gestionnaire
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
  FROM `role` r
  JOIN permission p
 WHERE r.nom = 'GESTIONNAIRE'
   AND p.nom IN ('CREER_COURS', 'MODIFIER_COURS', 'SUPPRIMER_COURS',
                 'ASSIGNER_SALLE', 'RESOUDRE_CONFLIT', 'GENERER_EDT',
                 'CONSULTER_EDT', 'CONSULTER_STATISTIQUES');

-- Enseignant
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
  FROM `role` r
  JOIN permission p
 WHERE r.nom = 'ENSEIGNANT'
   AND p.nom IN ('CONSULTER_EDT', 'RESERVER_SALLE', 'SIGNALER_PROBLEME');

-- Etudiant
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
  FROM `role` r
  JOIN permission p
 WHERE r.nom = 'ETUDIANT'
   AND p.nom IN ('CONSULTER_EDT', 'RECHERCHER_SALLE');

-- ------------------------------------------------------------
-- 3) Vues (corrigees)
-- ------------------------------------------------------------

DROP VIEW IF EXISTS vue_taux_occupation;
DROP VIEW IF EXISTS vue_permissions_utilisateur;
DROP VIEW IF EXISTS vue_emploi_du_temps;

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

-- ------------------------------------------------------------
-- 4) Verifications rapides
-- ------------------------------------------------------------

SELECT 'FK cours.enseignant_id -> enseignant(id)' AS check_name,
       COUNT(*) AS ok
  FROM information_schema.KEY_COLUMN_USAGE
 WHERE TABLE_SCHEMA = DATABASE()
   AND TABLE_NAME = 'cours'
   AND COLUMN_NAME = 'enseignant_id'
   AND REFERENCED_TABLE_NAME = 'enseignant';

SELECT 'RBAC links' AS check_name, COUNT(*) AS role_permission_rows
  FROM role_permission;
