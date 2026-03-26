package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private int id;
    private int destinataireId;
    private String message;
    private TypeNotification type;
    private LocalDateTime dateEnvoi;
    private boolean lue;

    public enum TypeNotification {
        CONFLIT("⚠️ Conflit"),
        CHANGEMENT_SALLE("🏫 Changement de salle"),
        RAPPEL_RESERVATION("🔔 Rappel réservation"),
        PROBLEME_TECHNIQUE("🔧 Problème technique");

        private final String libelle;
        TypeNotification(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public Notification() {
        this.dateEnvoi = LocalDateTime.now();
        this.lue = false;
    }

    public Notification(int destinataireId, String message, TypeNotification type) {
        this.destinataireId = destinataireId;
        this.message        = message;
        this.type           = type;
        this.dateEnvoi      = LocalDateTime.now();
        this.lue            = false;
    }

    public void marquerLue() {
        this.lue = true;
    }

    public void envoyer() {
        // À connecter à un service email ou alerte UI
        System.out.println("📨 Notification envoyée à #" + destinataireId + " : " + message);
    }

    public String getDateFormatee() {
        return dateEnvoi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinataireld() { return destinataireId; }
    public void setDestinataireld(int destinataireId) { this.destinataireId = destinataireId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public TypeNotification getType() { return type; }
    public void setType(TypeNotification type) { this.type = type; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }

    @Override
    public String toString() {
        return (type != null ? type.getLibelle() : "") +
                " | " + message +
                " | " + getDateFormatee() +
                (lue ? "" : " 🔵");
    }
}