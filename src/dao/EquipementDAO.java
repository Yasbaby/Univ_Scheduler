package dao;

import database.DatabaseConnection;
import model.Equipement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementDAO {

    public List<Equipement> getTousLesEquipements() {
        List<Equipement> liste = new ArrayList<>();
        String sql = "SELECT * FROM equipement";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setFonctionnel(rs.getBoolean("fonctionnel"));
                e.setSalleId(rs.getInt("salle_id"));
                liste.add(e);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return liste;
    }

    public boolean ajouter(Equipement eq) {
        String sql = "INSERT INTO equipement (nom, description, fonctionnel, salle_id) " +
                "VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString (1, eq.getNom());
            ps.setString (2, eq.getDescription());
            ps.setBoolean(3, eq.isFonctionnel());
            ps.setInt    (4, eq.getSalleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM equipement WHERE id = ?";
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

    public boolean setFonctionnel(int id, boolean fonctionnel) {
        String sql = "UPDATE equipement SET fonctionnel = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, fonctionnel);
            ps.setInt    (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            return false;
        }
    }
}