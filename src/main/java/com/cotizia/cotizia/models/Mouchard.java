package com.cotizia.cotizia.models;

import java.time.LocalDateTime;

public class Mouchard {
    private int id;
    private String action;
    private LocalDateTime dateAction;
    private String details;
    private Utilisateur utilisateur;

    public Mouchard() {
    }

    public Mouchard(String action, String details, Utilisateur utilisateur) {
        this.action = action;
        this.details = details;
        this.utilisateur = utilisateur;
        this.dateAction = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
