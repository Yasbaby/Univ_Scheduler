package dao;

import database.DatabaseConnection;
import model.Role;
import model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // =========================================================
    //  Connexion RBAC
    // =========================================================
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
                Utilisateur u = mapUtilisateur(rs, conn); // ✅ mapping centralisé
                return u;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    //  Mapping centralisé — remplace le code inline répété
    // =========================================================
    private Utilisateur mapUtilisateur(ResultSet rs, Connection conn) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setActif(rs.getBoolean("actif"));

        // Charger le rôle avec ses permissions
        RoleDAO roleDAO = new RoleDAO();
        Role role = roleDAO.getRoleAvecPermissions(rs.getInt("role_id"));
        u.setRole(role);

        // ✅ Charger classe + numero_etudiant depuis la table `etudiant`
        //    pour TOUS les utilisateurs qui ont une entrée dans etudiant
        chargerInfosEtudiant(u, conn);

        return u;
    }

    // =========================================================
    //  Charge classe + numeroEtudiant si l'utilisateur est étudiant
    // =========================================================
    private void chargerInfosEtudiant(Utilisateur u, Connection conn) {
        String sql = "SELECT classe, numero_etudiant FROM etudiant WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, u.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u.setClasse(rs.getString("classe"));
                u.setNumeroEtudiant(rs.getString("numero_etudiant"));
            }
        } catch (SQLException e) {
            // Pas étudiant = normal, on ignore silencieusement
        }
    }

    // =========================================================
    //  Tous les utilisateurs
    // =========================================================
    public List<Utilisateur> getTousLesUtilisateurs() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT u.*, r.nom AS role_nom " +
                "FROM utilisateur u JOIN `role` r ON u.role_id = r.id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Utilisateur u = mapUtilisateur(rs, conn); // ✅ même mapping
                liste.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return liste;
    }

    // =========================================================
    //  Ajouter un utilisateur
    // =========================================================
    public boolean ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setInt   (5, u.getRole().getId());

            if (ps.executeUpdate() > 0) {
                // ✅ Si étudiant, insérer aussi dans la table etudiant
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next() && u.getClasse() != null) {
                    int newId = keys.getInt(1);
                    insererEtudiant(newId, u.getClasse(),
                            u.getNumeroEtudiant(), conn);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout utilisateur : " + e.getMessage());
        }
        return false;
    }

    // =========================================================
    //  Insertion dans la table etudiant
    // =========================================================
    private void insererEtudiant(int userId, String classe,
                                 String numeroEtudiant, Connection conn) {
        String sql = "INSERT INTO etudiant (id, classe, numero_etudiant) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE classe = VALUES(classe), " +
                "numero_etudiant = VALUES(numero_etudiant)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt   (1, userId);
            ps.setString(2, classe);
            ps.setString(3, numeroEtudiant);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion etudiant : " + e.getMessage());
        }
    }

    // =========================================================
    //  Supprimer
    // =========================================================
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

    // =========================================================
    //  Activer / Désactiver
    // =========================================================
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

    // =========================================================
    //  Utilitaire : ID du gestionnaire
    // =========================================================
    public int getIdGestionnaire() {
        String sql = "SELECT u.id FROM utilisateur u " +
                "JOIN role r ON u.role_id = r.id " +
                "WHERE UPPER(r.nom) = 'GESTIONNAIRE' LIMIT 1";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("❌ Erreur getIdGestionnaire : " + e.getMessage());
        }
        return -1;
    }
}