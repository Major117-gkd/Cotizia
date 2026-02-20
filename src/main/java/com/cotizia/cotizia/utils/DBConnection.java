package com.cotizia.cotizia.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cotizia_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // A modifier selon la config

    private static Connection connection = null;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("DBConnection: Opening new connection to " + URL);
            try {
                // Charger le driver explicitement pour les anciennes versions,
                // mais optionnel pour les recentes. Utile pour NetBeans parfois.
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("DBConnection: Connection established.");
            } catch (ClassNotFoundException e) {
                System.err.println("DBConnection: Driver not found!");
                throw new SQLException("Driver MySQL non trouvé", e);
            } catch (SQLException e) {
                System.err.println("DBConnection: Connection FAILED! " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
}
