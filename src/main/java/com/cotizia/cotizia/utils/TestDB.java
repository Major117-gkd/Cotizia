package com.cotizia.cotizia.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("--- TEST CONNEXION BDD ---");
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("SUCCES: Connexion établie !");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("User: " + conn.getMetaData().getUserName());

                // Check users
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur");
                System.out.println("\n--- LISTE UTILISATEURS ---");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            " | Email: " + rs.getString("email") +
                            " | Pass: " + rs.getString("mot_de_passe") +
                            " | Role: " + rs.getString("role"));
                }

                conn.close();
            } else {
                System.out.println("ECHEC: La connexion est null.");
            }
        } catch (SQLException e) {
            System.out.println("ECHEC CRITIQUE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
