package com.cotizia.cotizia.services;

import com.cotizia.cotizia.models.Echeance;
import com.cotizia.cotizia.models.Participant;

public class NotificationService {

    public void sendPaymentReminder(Echeance echeance) {
        String email = echeance.getParticipant().getUtilisateur().getEmail();
        String message = "Rappel: Une échéance de " + echeance.getParticipant().getCycle().getMontantCotisation()
                + " est due le " + echeance.getDatePrevue();

        // Simulation
        System.out.println("[EMAIL " + email + "]: " + message);

        // Log to Mouchard? We would need dependency injection or static call to
        // MouchardDAO
    }

    public void sendWelcomeEmail(Participant participant) {
        String email = participant.getUtilisateur().getEmail();
        String message = "Bienvenue dans le cycle " + participant.getCycle().getNom();
        System.out.println("[EMAIL " + email + "]: " + message);
    }
}
