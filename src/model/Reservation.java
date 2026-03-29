package model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int demandeurId;
    private int salleId;
    private int creneauId;
    private String motif;
    private String statut;
    private LocalDateTime dateCreation;
    private String nomDemandeur;
    private String numeroSalle;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDemandeurId() { return demandeurId; }
    public void setDemandeurId(int demandeurId) { this.demandeurId = demandeurId; }

    public int getSalleId() { return salleId; }
    public void setSalleId(int salleId) { this.salleId = salleId; }

    public int getCreneauId() { return creneauId; }
    public void setCreneauId(int creneauId) { this.creneauId = creneauId; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getNomDemandeur() { return nomDemandeur; }
    public void setNomDemandeur(String nomDemandeur) { this.nomDemandeur = nomDemandeur; }

    public String getNumeroSalle() { return numeroSalle; }
    public void setNumeroSalle(String numeroSalle) { this.numeroSalle = numeroSalle; }
}