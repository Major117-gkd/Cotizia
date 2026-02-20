package com.cotizia.cotizia.interfaces;

import com.cotizia.cotizia.models.Utilisateur;
import java.util.List;

public interface IUtilisateurDAO {
    Utilisateur login(String email, String password);

    void create(Utilisateur utilisateur);

    void update(Utilisateur utilisateur);

    void delete(int id);

    Utilisateur findById(int id);

    List<Utilisateur> findAll();

    List<Utilisateur> findByRole(String role);
}
