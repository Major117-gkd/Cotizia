package com.cotizia.cotizia.interfaces;

import com.cotizia.cotizia.models.Participant;
import java.util.List;

public interface IParticipantDAO {
    void addParticipant(Participant participant);

    void removeParticipant(int id);

    List<Participant> findByCycle(int cycleId);

    Participant findByCycleAndUser(int cycleId, int userId);

    List<Participant> findByUtilisateur(int utilisateurId);
}
