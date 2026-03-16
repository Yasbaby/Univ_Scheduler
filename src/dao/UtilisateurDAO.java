package dao;

import database.DatabaseConnection;
import model.Utilisateur;
import java.sql.*;

public class UtilisateurDAO {

    // Vérifie les identifiants et retourne l'utilisateur si correct
    public Utilisateur seConnecter(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
        return null;
    }
}