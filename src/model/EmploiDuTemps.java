package model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmploiDuTemps {
    private int id;
    private String semestre;
    private int annee;
    private List<Cours> cours;

    public EmploiDuTemps() {
        this.cours = new ArrayList<>();
    }

    public EmploiDuTemps(String semestre, int annee) {
        this.semestre = semestre;
        this.annee    = annee;
        this.cours    = new ArrayList<>();
    }

    public void ajouterCours(Cours c) {
        this.cours.add(c);
    }

    public void supprimerCours(int id) {
        this.cours.removeIf(c -> c.getId() == id);
    }

    public List<Cours> getCoursParJour(DayOfWeek jour) {
        return cours.stream()
                .filter(c -> c.getCreneau() != null &&
                        c.getCreneau().getJour() == jour)
                .collect(Collectors.toList());
    }

    public List<Cours> getCoursParEnseignant(int enseignantId) {
        return cours.stream()
                .filter(c -> c.getEnseignantId() == enseignantId)
                .collect(Collectors.toList());
    }

    public List<Cours> getCoursParClasse(String groupe) {
        return cours.stream()
                .filter(c -> groupe.equals(c.getGroupe()))
                .collect(Collectors.toList());
    }

    public int getNombreCours() {
        return cours.size();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public List<Cours> getCours() { return cours; }
    public void setCours(List<Cours> cours) { this.cours = cours; }

    @Override
    public String toString() {
        return "Emploi du temps — " + semestre + " " + annee +
                " (" + cours.size() + " cours)";
    }
}