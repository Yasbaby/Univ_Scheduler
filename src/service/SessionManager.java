package service;

import model.Utilisateur;

public class SessionManager {

    // Instance unique (Singleton)
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void connecter(Utilisateur u) {
        this.utilisateurConnecte = u;
    }

    public void deconnecter() {
        this.utilisateurConnecte = null;
    }

    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    // Vérifie si l'utilisateur connecté a une permission
    public boolean aLaPermission(String permission) {
        if (!estConnecte()) return false;
        return utilisateurConnecte.aLaPermission(permission);
    }

    public String getRole() {
        if (!estConnecte()) return "";
        return utilisateurConnecte.getRole().getNom();
    }
}