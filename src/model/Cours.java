package model;

public class Cours {
    private int id;
    private String matiere;
    private String groupe;
    private String type; // CM, TD, TP, EXAMEN
    private int enseignantId;
    private int salleId;
    private int creneauId;
    private int emploiDuTempsId;

    // On stocke aussi les infos liées pour l'affichage
    private String nomEnseignant;
    private String numeroSalle;
    private Creneau creneau;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getGroupe() { return groupe; }
    public void setGroupe(String groupe) { this.groupe = groupe; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getEnseignantId() { return enseignantId; }
    public void setEnseignantId(int enseignantId) { this.enseignantId = enseignantId; }

    public int getSalleId() { return salleId; }
    public void setSalleId(int salleId) { this.salleId = salleId; }

    public int getCreneauId() { return creneauId; }
    public void setCreneauId(int creneauId) { this.creneauId = creneauId; }

    public int getEmploiDuTempsId() { return emploiDuTempsId; }
    public void setEmploiDuTempsId(int emploiDuTempsId) { this.emploiDuTempsId = emploiDuTempsId; }

    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }

    public String getNumeroSalle() { return numeroSalle; }
    public void setNumeroSalle(String numeroSalle) { this.numeroSalle = numeroSalle; }

    public Creneau getCreneau() { return creneau; }
    public void setCreneau(Creneau creneau) { this.creneau = creneau; }

    @Override
    public String toString() {
        return matiere + " | " + groupe + " | " + type +
                " | " + nomEnseignant + " | Salle " + numeroSalle +
                " | " + (creneau != null ? creneau.toString() : "");
    }


}