package dao;

import database.DatabaseConnection;
import model.Role;
import model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // Connexion avec vérification des permissions RBAC
    public Utilisateur seConnecter(String email, String motDePasse) {
        String sql = "SELECT u.*, r.nom AS role_nom " +
                "FROM utilisateur u " +
                "JOIN `role` r ON u.role_id = r.id " +
                "WHERE u.email = ? AND u.mot_de_passe = ? AND u.actif = TRUE";
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

                // Charger le rôle avec ses permissions
                RoleDAO roleDAO = new RoleDAO();
                Role role = roleDAO.getRoleAvecPermissions(rs.getInt("role_id"));
                u.setRole(role);
                return u;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
        return null;
    }

    // Récupère tous les utilisateurs
    public List<Utilisateur> getTousLesUtilisateurs() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT u.*, r.nom AS role_nom " +
                "FROM utilisateur u JOIN `role` r ON u.role_id = r.id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                Role r = new Role();
                r.setNom(rs.getString("role_nom"));
                u.setRole(r);
                u.setActif(rs.getBoolean("actif"));
                liste.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return liste;
    }

    // Ajouter un utilisateur
    public boolean ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setInt   (5, u.getRole().getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout utilisateur : " + e.getMessage());
            return false;
        }
    }

    // Supprimer un utilisateur
    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
            return false;
        }
    }

    // Activer/désactiver un utilisateur
    public boolean toggleActif(int id, boolean actif) {
        String sql = "UPDATE utilisateur SET actif = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, actif);
            ps.setInt    (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur toggle actif : " + e.getMessage());
            return false;
        }
    }
}
