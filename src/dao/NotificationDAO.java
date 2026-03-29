package dao;

import database.DatabaseConnection;
import model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<Notification> getMesNotifications(int utilisateurId) {
        List<Notification> liste = new ArrayList<>();
        String sql = "SELECT * FROM notification " +
                "WHERE destinataire_id = ? " +
                "ORDER BY date_envoi DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setLue(rs.getBoolean("lue"));
                n.setDateEnvoi(rs.getTimestamp("date_envoi").toLocalDateTime());
                liste.add(n);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return liste;
    }

    public int getNombreNonLues(int utilisateurId) {
        String sql = "SELECT COUNT(*) FROM notification " +
                "WHERE destinataire_id = ? AND lue = FALSE";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return 0;
    }

    public boolean marquerLue(int id) {
        String sql = "UPDATE notification SET lue = TRUE WHERE id = ?";
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

    public boolean toutMarquerLues(int utilisateurId) {
        String sql = "UPDATE notification SET lue = TRUE WHERE destinataire_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }
}