package dao;

import database.DatabaseConnection;
import model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getToutesLesReservations() {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "CONCAT(u.prenom, ' ', u.nom) AS nom_demandeur, " +
                "s.numero AS numero_salle " +
                "FROM reservation r " +
                "JOIN utilisateur u ON r.demandeur_id = u.id " +
                "JOIN salle s ON r.salle_id = s.id " +
                "ORDER BY r.created_at DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setDemandeurId(rs.getInt("demandeur_id"));
                r.setSalleId(rs.getInt("salle_id"));
                r.setMotif(rs.getString("motif"));
                r.setStatut(rs.getString("statut"));
                r.setNomDemandeur(rs.getString("nom_demandeur"));
                r.setNumeroSalle(rs.getString("numero_salle"));
                liste.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return liste;
    }

    public boolean changerStatut(int id, String statut) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // 1. Récupère les infos de la réservation
            String sqlGet = "SELECT r.demandeur_id, s.numero, r.motif " +
                    "FROM reservation r " +
                    "JOIN salle s ON r.salle_id = s.id " +
                    "WHERE r.id = ?";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, id);
            ResultSet rs = psGet.executeQuery();

            int demandeurId = 0;
            String numeroSalle = "";
            String motif = "";
            if (rs.next()) {
                demandeurId = rs.getInt("demandeur_id");
                numeroSalle = rs.getString("numero");
                motif       = rs.getString("motif");
            }

            // 2. Met à jour le statut
            String sqlUpdate = "UPDATE reservation SET statut = ? WHERE id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setString(1, statut);
            psUpdate.setInt   (2, id);
            psUpdate.executeUpdate();

            // 3. Crée la notification pour l'enseignant
            String message;
            if (statut.equals("VALIDEE")) {
                message = "✅ Votre réservation de la salle " + numeroSalle +
                        " pour \"" + motif + "\" a été VALIDÉE !";
            } else {
                message = "❌ Votre réservation de la salle " + numeroSalle +
                        " pour \"" + motif + "\" a été REFUSÉE.";
            }

            String sqlNotif = "INSERT INTO notification (destinataire_id, message, type) " +
                    "VALUES (?, ?, 'RAPPEL_RESERVATION')";
            PreparedStatement psNotif = conn.prepareStatement(sqlNotif);
            psNotif.setInt   (1, demandeurId);
            psNotif.setString(2, message);
            psNotif.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }

}