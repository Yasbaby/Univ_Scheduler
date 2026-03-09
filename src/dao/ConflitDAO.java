package dao;

import database.DatabaseConnection;
import java.sql.*;

public class ConflitDAO {

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

    // Vérifie si un enseignant est déjà occupé sur un créneau
    public boolean enseignantDejaOccupe(int enseignantId, int creneauId) {
        String sql = "SELECT COUNT(*) FROM cours " +
                "WHERE enseignant_id = ? AND creneau_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, enseignantId);
            ps.setInt(2, creneauId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return false;
    }

    // Vérifie si la capacité de la salle est suffisante
    public boolean capaciteInsuffisante(int salleId, int nombreEtudiants) {
        String sql = "SELECT capacite FROM salle WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int capacite = rs.getInt("capacite");
                return nombreEtudiants > capacite;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return false;
    }

    // Vérifie tous les conflits avant d'ajouter un cours
    public String verifierConflits(int salleId, int enseignantId,
                                   int creneauId, int nombreEtudiants) {
        if (salleDejaOccupee(salleId, creneauId)) {
            return "❌ CONFLIT : cette salle est déjà occupée sur ce créneau !";
        }
        if (enseignantDejaOccupe(enseignantId, creneauId)) {
            return "❌ CONFLIT : cet enseignant a déjà un cours sur ce créneau !";
        }
        if (capaciteInsuffisante(salleId, nombreEtudiants)) {
            return "❌ CONFLIT : la salle est trop petite pour ce nombre d'étudiants !";
        }
        return "✅ Aucun conflit détecté !";
    }
}