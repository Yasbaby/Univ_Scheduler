package model;

public class Equipement {
    private int id;
    private String nom;
    private String description;
    private boolean fonctionnel;
    private int salleId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isFonctionnel() { return fonctionnel; }
    public void setFonctionnel(boolean fonctionnel) { this.fonctionnel = fonctionnel; }

    public int getSalleId() { return salleId; }
    public void setSalleId(int salleId) { this.salleId = salleId; }

    @Override
    public String toString() {
        return nom + " — " + (fonctionnel ? "Fonctionnel" : "En panne");
    }
}