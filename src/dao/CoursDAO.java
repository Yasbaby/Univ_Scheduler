package dao;

import database.DatabaseConnection;
import model.Cours;
import model.Creneau;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {

    // Récupère tous les cours avec leurs détails
    public List<Cours> getTousLesCours() {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "CONCAT(u.prenom, ' ', u.nom) AS nom_enseignant, " +
                "s.numero AS numero_salle, " +
                "cr.jour, cr.heure_debut, cr.heure_fin " +
                "FROM cours c " +
                "JOIN utilisateur u  ON c.enseignant_id = u.id " +
                "JOIN salle s        ON c.salle_id = s.id " +
                "JOIN creneau cr     ON c.creneau_id = cr.id";

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                Cours c = new Cours();
                c.setId(rs.getInt("id"));
                c.setMatiere(rs.getString("matiere"));
                c.setGroupe(rs.getString("groupe"));
                c.setType(rs.getString("type"));
                c.setEnseignantId(rs.getInt("enseignant_id"));
                c.setSalleId(rs.getInt("salle_id"));
                c.setNomEnseignant(rs.getString("nom_enseignant"));
                c.setNumeroSalle(rs.getString("numero_salle"));

                // Récupère le créneau
                Creneau cr = new Creneau();
                cr.setJour(convertirJour(rs.getString("jour")));
                cr.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                cr.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                c.setCreneau(cr);

                coursList.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return coursList;
    }

    // Ajoute un nouveau cours
    public boolean ajouter(Cours c) {
        String sql = "INSERT INTO cours (matiere, groupe, type, enseignant_id, " +
                "salle_id, creneau_id, emploi_du_temps_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, c.getMatiere());
            ps.setString(2, c.getGroupe());
            ps.setString(3, c.getType());
            ps.setInt   (4, c.getEnseignantId());
            ps.setInt   (5, c.getSalleId());
            ps.setInt   (6, c.getCreneauId());
            ps.setInt   (7, c.getEmploiDuTempsId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }

    // Supprime un cours
    public boolean supprimer(int id) {
        String sql = "DELETE FROM cours WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }

    // Vérifie si une salle est déjà occupée sur un créneau
    public boolean salleDejaOccupee(int salleId, int creneauId) {
        String sql = "SELECT COUNT(*) FROM cours " +
                "WHERE salle_id = ? AND creneau_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salleId);
            ps.setInt(2, creneauId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return false;
    }
    // Convertit le jour français (MySQL) en DayOfWeek Java
    private DayOfWeek convertirJour(String jour) {
        switch (jour) {
            case "LUNDI":     return DayOfWeek.MONDAY;
            case "MARDI":     return DayOfWeek.TUESDAY;
            case "MERCREDI":  return DayOfWeek.WEDNESDAY;
            case "JEUDI":     return DayOfWeek.THURSDAY;
            case "VENDREDI":  return DayOfWeek.FRIDAY;
            case "SAMEDI":    return DayOfWeek.SATURDAY;
            case "DIMANCHE":  return DayOfWeek.SUNDAY;
            default:          return null;
        }
    }
    // ✅ Tous les groupes distincts
    public List<String> getTousLesGroupes() {
        List<String> groupes = new ArrayList<>();
        String sql = "SELECT DISTINCT groupe FROM cours ORDER BY groupe";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                groupes.add(rs.getString("groupe"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getTousLesGroupes : " + e.getMessage());
        }
        return groupes;
    }

    // ✅ Cours filtrés par groupe/classe
    public List<Cours> getCoursParGroupe(String groupe) {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "CONCAT(u.prenom, ' ', u.nom) AS nom_enseignant, " +
                "s.numero AS numero_salle, " +
                "cr.jour, cr.heure_debut, cr.heure_fin " +
                "FROM cours c " +
                "LEFT JOIN utilisateur u ON c.enseignant_id = u.id " +
                "LEFT JOIN salle s       ON c.salle_id = s.id " +
                "LEFT JOIN creneau cr    ON c.creneau_id = cr.id " +
                "WHERE c.groupe = ? " +
                "ORDER BY cr.jour, cr.heure_debut";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, groupe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapCours(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getCoursParGroupe : " + e.getMessage());
        }
        return liste;
    }

    // ✅ Cours filtrés par enseignant
    public List<Cours> getCoursParEnseignant(int enseignantId) {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "CONCAT(u.prenom, ' ', u.nom) AS nom_enseignant, " +
                "s.numero AS numero_salle, " +
                "cr.jour, cr.heure_debut, cr.heure_fin " +
                "FROM cours c " +
                "LEFT JOIN utilisateur u ON c.enseignant_id = u.id " +
                "LEFT JOIN salle s       ON c.salle_id = s.id " +
                "LEFT JOIN creneau cr    ON c.creneau_id = cr.id " +
                "WHERE c.enseignant_id = ? " +
                "ORDER BY cr.jour, cr.heure_debut";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapCours(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getCoursParEnseignant : " + e.getMessage());
        }
        return liste;
    }

    private Cours mapCours(ResultSet rs) throws SQLException {
        Cours c = new Cours();
        c.setId(rs.getInt("id"));
        c.setMatiere(rs.getString("matiere"));
        c.setGroupe(rs.getString("groupe"));
        c.setType(rs.getString("type"));
        c.setEnseignantId(rs.getInt("enseignant_id"));
        c.setSalleId(rs.getInt("salle_id"));
        c.setCreneauId(rs.getInt("creneau_id"));
        c.setNomEnseignant(rs.getString("nom_enseignant"));
        c.setNumeroSalle(rs.getString("numero_salle"));

        try {
            String jourStr = rs.getString("jour");
            java.sql.Time heureDebut = rs.getTime("heure_debut");
            java.sql.Time heureFin   = rs.getTime("heure_fin");

            if (jourStr != null && heureDebut != null && heureFin != null) {
                Creneau cr = new Creneau();
                cr.setJour(convertirJour(jourStr));   // ✅ méthode déjà existante
                cr.setHeureDebut(heureDebut.toLocalTime());
                cr.setHeureFin(heureFin.toLocalTime());
                c.setCreneau(cr);
            }
        } catch (Exception e) {
            c.setCreneau(null);
        }

        return c;
    }
}