package com.cotizia.cotizia.interfaces;

import com.cotizia.cotizia.models.Echeance;
import java.util.List;

public interface IEcheanceDAO {
    void create(Echeance echeance);

    void update(Echeance echeance); // Pour payer

    List<Echeance> findByParticipant(int participantId);

    List<Echeance> findByCycle(int cycleId); // Pour voir l'etat general
}
