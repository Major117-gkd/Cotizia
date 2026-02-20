package com.cotizia.cotizia.implementation;

import com.cotizia.cotizia.interfaces.IEcheanceDAO;
import com.cotizia.cotizia.models.Echeance;
import com.cotizia.cotizia.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EcheanceDAO implements IEcheanceDAO {

    private ParticipantDAO participantDAO = new ParticipantDAO();

    public void create(Echeance echeance) {
        String sql = "INSERT INTO echeance (participant_id, date_prevue, date_paiement, montant_paye, statut) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, echeance.getParticipant().getId());
            pstmt.setDate(2, Date.valueOf(echeance.getDatePrevue()));
            pstmt.setDate(3, echeance.getDatePaiement() != null ? Date.valueOf(echeance.getDatePaiement()) : null);
            pstmt.setDouble(4, echeance.getMontantPaye());
            pstmt.setString(5, echeance.getStatut());

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

    public void update(Echeance echeance) {
        String sql = "UPDATE echeance SET date_paiement=?, montant_paye=?, statut=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setDate(1, echeance.getDatePaiement() != null ? Date.valueOf(echeance.getDatePaiement()) : null);
            pstmt.setDouble(2, echeance.getMontantPaye());
            pstmt.setString(3, echeance.getStatut());
            pstmt.setInt(4, echeance.getId());

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

    public List<Echeance> findByParticipant(int participantId) {
        System.out.println("EcheanceDAO: findByParticipant(ID=" + participantId + ") STARTED");
        List<Echeance> echeances = new ArrayList<Echeance>();
        String sql = "SELECT * FROM echeance WHERE participant_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, participantId);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                echeances.add(mapResultSetToEcheance(rs));
                count++;
            }
            System.out.println("EcheanceDAO: Found " + count + " echeances for participant " + participantId);
        } catch (SQLException e) {
            System.err.println("EcheanceDAO: Error in findByParticipant: " + e.getMessage());
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
        return echeances;
    }

    public List<Echeance> findByCycle(int cycleId) {
        List<Echeance> echeances = new ArrayList<Echeance>();
        String sql = "SELECT e.* FROM echeance e JOIN participant p ON e.participant_id = p.id WHERE p.cycle_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cycleId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                echeances.add(mapResultSetToEcheance(rs));
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
        return echeances;
    }

    private Echeance mapResultSetToEcheance(ResultSet rs) throws SQLException {
        Echeance e = new Echeance();
        e.setId(rs.getInt("id"));

        int participantId = rs.getInt("participant_id");
        e.setParticipant(participantDAO.findById(participantId));

        Date datePrevue = rs.getDate("date_prevue");
        if (datePrevue != null) {
            e.setDatePrevue(datePrevue.toLocalDate());
        }

        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            e.setDatePaiement(datePaiement.toLocalDate());
        }

        e.setMontantPaye(rs.getDouble("montant_paye"));
        e.setStatut(rs.getString("statut"));

        return e;
    }

    public double sumTotalPaid() {
        String sql = "SELECT SUM(montant_paye) FROM echeance WHERE statut = 'PAYE'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble(1);
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
        return 0.0;
    }
}
