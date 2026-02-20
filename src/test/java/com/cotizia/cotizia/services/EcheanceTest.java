package com.cotizia.cotizia.services;

import com.cotizia.cotizia.models.Echeance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EcheanceTest {

    @Test
    public void testEcheanceInitialStatus() {
        Echeance e = new Echeance();
        e.setStatut("EN_ATTENTE");
        assertEquals("EN_ATTENTE", e.getStatut());
    }

    // Additional logic tests if applicable
}
