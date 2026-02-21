package com.cotizia.cotizia.implementation;

import com.cotizia.cotizia.interfaces.ICycleDAO;
import com.cotizia.cotizia.models.Collecteur;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CycleDAO implements ICycleDAO {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public void create(Cycle cycle) {
        String sql = "INSERT INTO cycle (nom, montant_cotisation, date_debut, date_fin, frequence, etat, collecteur_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, cycle.getNom());
            pstmt.setDouble(2, cycle.getMontantCotisation());
            pstmt.setDate(3, Date.valueOf(cycle.getDateDebut()));
            pstmt.setDate(4, cycle.getDateFin() != null ? Date.valueOf(cycle.getDateFin()) : null);
            pstmt.setString(5, cycle.getFrequence());
            pstmt.setString(6, cycle.getEtat());
            pstmt.setInt(7, cycle.getCollecteur().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = null;
                try {
                    generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        cycle.setId(generatedKeys.getInt(1));
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

    public void update(Cycle cycle) {
        String sql = "UPDATE cycle SET nom=?, montant_cotisation=?, date_debut=?, date_fin=?, frequence=?, etat=?, collecteur_id=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cycle.getNom());
            pstmt.setDouble(2, cycle.getMontantCotisation());
            pstmt.setDate(3, Date.valueOf(cycle.getDateDebut()));
            pstmt.setDate(4, cycle.getDateFin() != null ? Date.valueOf(cycle.getDateFin()) : null);
            pstmt.setString(5, cycle.getFrequence());
            pstmt.setString(6, cycle.getEtat());
            pstmt.setInt(7, cycle.getCollecteur().getId());
            pstmt.setInt(8, cycle.getId());

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
        String sql = "DELETE FROM cycle WHERE id = ?";
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

    public Cycle findById(int id) {
        String sql = "SELECT * FROM cycle WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCycle(rs);
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

    public List<Cycle> findAll() {
        List<Cycle> cycles = new ArrayList<>();
        String sql = "SELECT * FROM cycle";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cycles.add(mapResultSetToCycle(rs));
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
        return cycles;
    }

    public List<Cycle> findByCollecteur(int collecteurId) {
        List<Cycle> cycles = new ArrayList<>();
        String sql = "SELECT * FROM cycle WHERE collecteur_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, collecteurId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                cycles.add(mapResultSetToCycle(rs));
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
        return cycles;
    }

    private Cycle mapResultSetToCycle(ResultSet rs) throws SQLException {
        Cycle cycle = new Cycle();
        cycle.setId(rs.getInt("id"));
        cycle.setNom(rs.getString("nom"));
        cycle.setMontantCotisation(rs.getDouble("montant_cotisation"));
        cycle.setDateDebut(rs.getDate("date_debut").toLocalDate());
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            cycle.setDateFin(dateFin.toLocalDate());
        }
        cycle.setFrequence(rs.getString("frequence"));
        cycle.setEtat(rs.getString("etat"));

        // Fetch Collecteur
        int collecteurId = rs.getInt("collecteur_id");
        cycle.setCollecteur((Collecteur) utilisateurDAO.findById(collecteurId));

        return cycle;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM cycle";
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
