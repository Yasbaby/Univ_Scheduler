package model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int demandeurId;
    private int salleId;
    private int creneauId;
    private String motif;
    private StatutReservation statut;
    private LocalDateTime dateCreation;

    // Infos liées pour l'affichage
    private String nomDemandeur;
    private String numeroSalle;
    private Creneau creneau;

    public enum StatutReservation {
        EN_ATTENTE, VALIDEE, ANNULEE, EXPIREE
    }

    public Reservation() {
        this.statut = StatutReservation.EN_ATTENTE;
        this.dateCreation = LocalDateTime.now();
    }

    public void valider() {
        this.statut = StatutReservation.VALIDEE;
    }

    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }

    public String getDetails() {
        return "Réservation #" + id +
                " | " + nomDemandeur +
                " | Salle " + numeroSalle +
                " | " + (creneau != null ? creneau.toString() : "") +
                " | " + statut;
    }

    // Getters et Setters
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

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getNomDemandeur() { return nomDemandeur; }
    public void setNomDemandeur(String nomDemandeur) { this.nomDemandeur = nomDemandeur; }

    public String getNumeroSalle() { return numeroSalle; }
    public void setNumeroSalle(String numeroSalle) { this.numeroSalle = numeroSalle; }

    public Creneau getCreneau() { return creneau; }
    public void setCreneau(Creneau creneau) { this.creneau = creneau; }

    @Override
    public String toString() {
        return getDetails();
    }
}