package com.cotizia.cotizia.models;

import java.time.LocalDate;

public class Cycle {
    private int id;
    private String nom;
    private double montantCotisation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String frequence; // HEBDOMADAIRE, MENSUELLE
    private String etat; // EN_COURS, CLOTURE
    private Collecteur collecteur;

    public Cycle() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getMontantCotisation() {
        return montantCotisation;
    }

    public void setMontantCotisation(double montantCotisation) {
        this.montantCotisation = montantCotisation;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Collecteur getCollecteur() {
        return collecteur;
    }

    public void setCollecteur(Collecteur collecteur) {
        this.collecteur = collecteur;
    }
}
