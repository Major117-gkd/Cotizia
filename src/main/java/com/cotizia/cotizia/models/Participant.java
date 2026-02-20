package com.cotizia.cotizia.models;

import java.time.LocalDate;

public class Participant {
    private int id;
    private Cycle cycle;
    private Utilisateur utilisateur;
    private LocalDate dateInscription;
    private int positionBeneficiaire;

    public Participant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public int getPositionBeneficiaire() {
        return positionBeneficiaire;
    }

    public void setPositionBeneficiaire(int positionBeneficiaire) {
        this.positionBeneficiaire = positionBeneficiaire;
    }
}
