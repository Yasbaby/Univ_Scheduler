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
                s.setDisponible(rs.getBoolean("disponible"));
                salles.add(s);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }

        return salles;
    }
}