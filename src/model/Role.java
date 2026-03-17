package model;

import java.util.List;
import java.util.ArrayList;

public class Role {
    private int id;
    private String nom;
    private String description;
    private List<String> permissions = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public boolean aLaPermission(String permission) {
        return permissions.contains(permission);
    }

    @Override
    public String toString() { return nom; }
}