package model;

import java.time.LocalTime;
import java.time.DayOfWeek;

public class Creneau {
    private int id;
    private DayOfWeek jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public DayOfWeek getJour() { return jour; }
    public void setJour(DayOfWeek jour) { this.jour = jour; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    // Vérifie si deux créneaux se chevauchent
    public boolean chevauche(Creneau autre) {
        return this.heureDebut.isBefore(autre.heureFin) &&
                autre.heureDebut.isBefore(this.heureFin);
    }

    // Durée en minutes
    public int getDuree() {
        return (int) java.time.Duration.between(heureDebut, heureFin).toMinutes();
    }

    @Override
    public String toString() {
        return jour + " | " + heureDebut + " → " + heureFin;
    }
}