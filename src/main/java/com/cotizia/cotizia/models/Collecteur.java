package com.cotizia.cotizia.models;

public class Collecteur extends Utilisateur {
    private String matricule;

    public Collecteur() {
        super();
        this.setRole("COLLECTEUR");
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
}
