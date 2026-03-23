package dao;

import database.DatabaseConnection;
import model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    // Récupère un rôle avec toutes ses permissions
    public Role getRoleAvecPermissions(int roleId) {
        Role role = new Role();
        String sql = "SELECT r.id, r.nom, r.description, p.nom AS permission " +
                "FROM `role` r " +
                "JOIN role_permission rp ON r.id = rp.role_id " +
                "JOIN permission p ON rp.permission_id = p.id " +
                "WHERE r.id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();

            List<String> permissions = new ArrayList<>();
            while (rs.next()) {
                role.setId(rs.getInt("id"));
                role.setNom(rs.getString("nom"));
                role.setDescription(rs.getString("description"));
                permissions.add(rs.getString("permission"));
            }
            role.setPermissions(permissions);
        } catch (SQLException e) {
            System.err.println("❌ Erreur RoleDAO : " + e.getMessage());
        }
        return role;
    }

    // Récupère tous les rôles
    public List<Role> getTousLesRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM `role`";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Role r = new Role();
                r.setId(rs.getInt("id"));
                r.setNom(rs.getString("nom"));
                r.setDescription(rs.getString("description"));
                roles.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur RoleDAO : " + e.getMessage());
        }
        return roles;
    }
}
