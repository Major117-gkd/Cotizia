package com.cotizia.cotizia.implementation;

import com.cotizia.cotizia.interfaces.IUtilisateurDAO;
import com.cotizia.cotizia.models.Adherant;
import com.cotizia.cotizia.models.Collecteur;
import com.cotizia.cotizia.models.Utilisateur;
import com.cotizia.cotizia.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO implements IUtilisateurDAO {

    public Utilisateur login(String email, String password) {
        System.out.println("UtilisateurDAO: Attempting login for email: " + email);
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String hashedInput = com.cotizia.cotizia.utils.SecurityUtils.hashPassword(password);

            // 1. Essayer avec le mot de passe haché (standard)
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, hashedInput);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("UtilisateurDAO: User found with hashed password.");
                return mapResultSetToUtilisateur(rs);
            }

            // 2. Essayer avec le mot de passe en texte clair (Migration des anciens
            // comptes)
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("UtilisateurDAO: User found with plain text password. Migrating to Hash...");
                Utilisateur user = mapResultSetToUtilisateur(rs);
                // Mettre à jour en base avec le mot de passe haché
                user.setMotDePasse(password); // Sera haché dans la méthode update()
                update(user);
                return user;
            }

            System.out.println("UtilisateurDAO: No user found with those credentials.");
        } catch (SQLException e) {
            System.err.println("UtilisateurDAO: Login SQLException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
        return null;
    }

    public void create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, telephone, adresse, matricule) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, com.cotizia.cotizia.utils.SecurityUtils.hashPassword(utilisateur.getMotDePasse()));
            pstmt.setString(5, utilisateur.getRole());
            pstmt.setString(6, utilisateur.getTelephone());
            pstmt.setString(7, utilisateur.getAdresse());

            if (utilisateur instanceof Collecteur) {
                pstmt.setString(8, ((Collecteur) utilisateur).getMatricule());
            } else {
                pstmt.setString(8, null);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = null;
                try {
                    generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        utilisateur.setId(generatedKeys.getInt(1));
                    }
                } finally {
                    if (generatedKeys != null)
                        generatedKeys.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
    }

    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, telephone=?, adresse=?, matricule=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, com.cotizia.cotizia.utils.SecurityUtils.hashPassword(utilisateur.getMotDePasse()));
            pstmt.setString(5, utilisateur.getTelephone());
            pstmt.setString(6, utilisateur.getAdresse());

            if (utilisateur instanceof Collecteur) {
                pstmt.setString(7, ((Collecteur) utilisateur).getMatricule());
            } else {
                pstmt.setString(7, null);
            }

            pstmt.setInt(8, utilisateur.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
    }

    public Utilisateur findById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
        return null;
    }

    public java.util.List findAll() {
        java.util.List utilisateurs = new java.util.ArrayList();
        String sql = "SELECT * FROM utilisateur";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
            }
        }
        return utilisateurs;
    }

    public java.util.List findByRole(String role) {
        java.util.List utilisateurs = new java.util.ArrayList();
        String sql = "SELECT * FROM utilisateur WHERE role = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
        return utilisateurs;
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        Utilisateur user;

        if ("COLLECTEUR".equalsIgnoreCase(role)) {
            user = new Collecteur();
            ((Collecteur) user).setMatricule(rs.getString("matricule"));
        } else if ("ADHERANT".equalsIgnoreCase(role)) {
            user = new Adherant();
        } else {
            user = new Utilisateur(); // Admin or generic
        }

        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setRole(role);
        user.setTelephone(rs.getString("telephone"));
        user.setAdresse(rs.getString("adresse"));

        return user;
    }

    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM utilisateur";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
            }
        }
        return 0;
    }
}
