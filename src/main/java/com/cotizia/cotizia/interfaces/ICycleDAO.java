package com.cotizia.cotizia.interfaces;

import com.cotizia.cotizia.models.Cycle;
import java.util.List;

public interface ICycleDAO {
    void create(Cycle cycle);

    void update(Cycle cycle);

    void delete(int id);

    Cycle findById(int id);

    List<Cycle> findAll();

    List<Cycle> findByCollecteur(int collecteurId);
}
