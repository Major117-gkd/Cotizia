package com.cotizia.cotizia.services;

import com.cotizia.cotizia.implementation.UtilisateurDAO;
import com.cotizia.cotizia.interfaces.IUtilisateurDAO;
import com.cotizia.cotizia.models.Utilisateur;

public class AuthenticationService {

    private IUtilisateurDAO utilisateurDAO;
    private static Utilisateur currentUser;

    public AuthenticationService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public boolean login(String email, String password) {
        Utilisateur user = utilisateurDAO.login(email, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean isCollecteur() {
        return currentUser != null && "COLLECTEUR".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }
}
