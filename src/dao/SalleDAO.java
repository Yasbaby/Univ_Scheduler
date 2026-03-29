package dao;

import database.DatabaseConnection;
import model.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> getToutesLesSalles() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle";

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                Salle s = new Salle();
                s.setId(rs.getInt("id"));
                s.setNumero(rs.getString("numero"));
                s.setCapacite(rs.getInt("capacite"));
                s.setType(rs.getString("type"));
                s.setBatimentId(rs.getInt("batiment_id"));
                s.setDisponible(rs.getBoolean("disponible"));
                salles.add(s);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }

        return salles;
    }

    public boolean ajouterSalle(Salle salle) {
        String sql = "INSERT INTO salle (numero, capacite, type, batiment_id) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, salle.getNumero());
            ps.setInt   (2, salle.getCapacite());
            ps.setString(3, salle.getType());
            ps.setInt   (4, salle.getBatimentId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }
    public boolean supprimer(int id) {
        String sql = "DELETE FROM salle WHERE id = ?";
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


}