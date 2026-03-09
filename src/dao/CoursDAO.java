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
}