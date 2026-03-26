package model;

public class Conflit {
    private int id;
    private String description;
    private TypeConflit type;
    private int cours1Id;
    private int cours2Id;
    private boolean resolu;

    // Infos liées pour l'affichage
    private String matiere1;
    private String matiere2;

    public enum TypeConflit {
        SALLE_OCCUPEE("Salle déjà occupée"),
        ENSEIGNANT_INDISPONIBLE("Enseignant indisponible"),
        CAPACITE_INSUFFISANTE("Capacité insuffisante");

        private final String libelle;
        TypeConflit(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public Conflit() {
        this.resolu = false;
    }

    public Conflit(TypeConflit type, int cours1Id, int cours2Id) {
        this.type     = type;
        this.cours1Id = cours1Id;
        this.cours2Id = cours2Id;
        this.resolu   = false;
        this.description = type.getLibelle();
    }

    public void resoudre() {
        this.resolu = true;
    }

    public String getDescription() {
        return "[" + (type != null ? type.getLibelle() : "?") + "] " +
                "Cours #" + cours1Id +
                (cours2Id > 0 ? " ↔ Cours #" + cours2Id : "") +
                " — " + (resolu ? "✅ Résolu" : "❌ En attente");
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public TypeConflit getType() { return type; }
    public void setType(TypeConflit type) { this.type = type; }

    public int getCours1Id() { return cours1Id; }
    public void setCours1Id(int cours1Id) { this.cours1Id = cours1Id; }

    public int getCours2Id() { return cours2Id; }
    public void setCours2Id(int cours2Id) { this.cours2Id = cours2Id; }

    public boolean isResolu() { return resolu; }
    public void setResolu(boolean resolu) { this.resolu = resolu; }

    public String getMatiere1() { return matiere1; }
    public void setMatiere1(String matiere1) { this.matiere1 = matiere1; }

    public String getMatiere2() { return matiere2; }
    public void setMatiere2(String matiere2) { this.matiere2 = matiere2; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return getDescription(); }
}