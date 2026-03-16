package model;

public class Salle {
    private int id;
    private String numero;
    private int capacite;
    private String type;
    private boolean disponible;
    private int batimentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }


    public int getBatimentId() { return batimentId; }
    public void setBatimentId(int batimentId) { this.batimentId = batimentId; }

    @Override
    public String toString() {
        return "Salle{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", capacite=" + capacite +
                ", type='" + type + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
