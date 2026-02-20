package com.cotizia.cotizia.utils;

import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.models.Utilisateur;

public class SelectionContext {
    private static SelectionContext instance;
    private Cycle selectedCycle;
    private Participant selectedParticipant;
    private Utilisateur selectedUser;

    private SelectionContext() {
    }

    public static SelectionContext getInstance() {
        if (instance == null) {
            instance = new SelectionContext();
        }
        return instance;
    }

    public Cycle getSelectedCycle() {
        return selectedCycle;
    }

    public void setSelectedCycle(Cycle selectedCycle) {
        this.selectedCycle = selectedCycle;
    }

    public Participant getSelectedParticipant() {
        return selectedParticipant;
    }

    public void setSelectedParticipant(Participant selectedParticipant) {
        this.selectedParticipant = selectedParticipant;
    }

    public Utilisateur getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Utilisateur selectedUser) {
        this.selectedUser = selectedUser;
    }
}
