package com.cotizia.cotizia.services;

import com.cotizia.cotizia.implementation.CycleDAO;
import com.cotizia.cotizia.implementation.EcheanceDAO;
import com.cotizia.cotizia.implementation.ParticipantDAO;
import com.cotizia.cotizia.interfaces.ICycleDAO;
import com.cotizia.cotizia.interfaces.IEcheanceDAO;
import com.cotizia.cotizia.interfaces.IParticipantDAO;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Echeance;
import com.cotizia.cotizia.models.Participant;
import java.time.LocalDate;
import java.util.List;

public class CycleService {

    private ICycleDAO cycleDAO = new CycleDAO();
    private IParticipantDAO participantDAO = new ParticipantDAO();
    private IEcheanceDAO echeanceDAO = new EcheanceDAO();

    public void creerCycle(Cycle cycle) {
        // Validation data
        if (cycle.getDateDebut() == null || cycle.getMontantCotisation() <= 0) {
            throw new IllegalArgumentException("Données cycle invalides");
        }
        cycleDAO.create(cycle);
    }

    public void ajouterParticipant(Cycle cycle, Participant participant) {
        System.out.println("CycleService: Adding participant to cycle: " + cycle.getNom());
        // Verifier capacite ou regles
        participantDAO.addParticipant(participant);
        System.out.println("CycleService: Participant added with ID: " + participant.getId());

        // Generer echeances pour ce participant ?
        System.out.println("CycleService: Generating echeances for participant " + participant.getId());
        genererEcheances(participant);
        System.out.println("CycleService: Echeances generation completed.");
    }

    public void genererEcheances(Participant participant) {
        Cycle cycle = participant.getCycle();
        // Logique simple: par exemple 10 echeances
        // A adapter selon la frequence et duree du cycle
        int nbEcheances = 10;
        LocalDate date = cycle.getDateDebut();

        for (int i = 0; i < nbEcheances; i++) {
            Echeance e = new Echeance();
            e.setParticipant(participant);
            e.setDatePrevue(date);
            e.setMontantPaye(0);
            e.setStatut("EN_ATTENTE");

            echeanceDAO.create(e);

            if ("HEBDOMADAIRE".equalsIgnoreCase(cycle.getFrequence())) {
                date = date.plusWeeks(1);
            } else {
                date = date.plusMonths(1);
            }
        }
    }

    public List<Cycle> getCyclesForCollecteur(int collecteurId) {
        return cycleDAO.findByCollecteur(collecteurId);
    }

    public List<Participant> getParticipants(int cycleId) {
        return participantDAO.findByCycle(cycleId);
    }

    public void supprimerParticipant(int participantId) {
        System.out.println("CycleService: Removing participant ID: " + participantId);
        participantDAO.removeParticipant(participantId);
    }

    public void checkAndUpdateLatePayments() {
        System.out.println("CycleService: Checking for late payments...");
        String sql = "UPDATE echeance SET statut = 'RETARD' WHERE statut = 'EN_ATTENTE' AND date_prevue < ?";
        try (java.sql.Connection conn = com.cotizia.cotizia.utils.DBConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("CycleService: " + updated + " echeances marked as RETARD.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("CycleService: Error checking late payments: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
