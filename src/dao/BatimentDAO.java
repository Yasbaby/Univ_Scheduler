package dao;

import database.DatabaseConnection;
import model.Batiment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatimentDAO {

    // Récupère tous les bâtiments
    public List<Batiment> getTousLesBatiments() {
        List<Batiment> batiments = new ArrayList<>();
        String sql = "SELECT * FROM batiment";

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                Batiment b = new Batiment();
                b.setId(rs.getInt("id"));
                b.setNom(rs.getString("nom"));
                b.setLocalisation(rs.getString("localisation"));
                b.setNombreEtages(rs.getInt("nombre_etages"));
                batiments.add(b);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return batiments;
    }

    // Ajoute un nouveau bâtiment
    public boolean ajouter(Batiment b) {
        String sql = "INSERT INTO batiment (nom, localisation, nombre_etages) VALUES (?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getLocalisation());
            ps.setInt   (3, b.getNombreEtages());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }

    // Supprime un bâtiment par son id
    public boolean supprimer(int id) {
        String sql = "DELETE FROM batiment WHERE id = ?";

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