package com.cotizia.cotizia.implementation;

import com.cotizia.cotizia.interfaces.IParticipantDAO;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDAO implements IParticipantDAO {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private CycleDAO cycleDAO = new CycleDAO();

    public void addParticipant(Participant participant) {
        String sql = "INSERT INTO participant (cycle_id, utilisateur_id, date_inscription, position_beneficiaire) VALUES (?, ?, ?, ?)";
        System.out.println("ParticipantDAO: Adding participant. CycleID=" + participant.getCycle().getId() + ", UserID="
                + participant.getUtilisateur().getId());
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, participant.getCycle().getId());
            pstmt.setInt(2, participant.getUtilisateur().getId());
            pstmt.setDate(3, Date.valueOf(participant.getDateInscription()));
            pstmt.setInt(4, participant.getPositionBeneficiaire());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("ParticipantDAO: INSERT executed. AffectedRows=" + affectedRows);
            if (affectedRows > 0) {
                ResultSet generatedKeys = null;
                try {
                    generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        participant.setId(id);
                        System.out.println("ParticipantDAO: Generated ID=" + id);
                    }
                } finally {
                    if (generatedKeys != null)
                        generatedKeys.close();
                }
            }
        } catch (SQLException e) {
            System.err.println("ParticipantDAO: CRITICAL ERROR in addParticipant: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du participant", e);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
            }
        }
    }

    public void removeParticipant(int id) {
        String sql = "DELETE FROM participant WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("ParticipantDAO: Deleted participant ID=" + id);
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

    public List<Participant> findByCycle(int cycleId) {
        System.out.println("ParticipantDAO: Searching for participants in cycle ID=" + cycleId);
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT * FROM participant WHERE cycle_id = ? ORDER BY position_beneficiaire ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cycleId);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                participants.add(mapResultSetToParticipant(rs));
                count++;
            }
            System.out.println("ParticipantDAO: findByCycle(" + cycleId + ") found " + count + " rows.");
        } catch (SQLException e) {
            System.err.println("ParticipantDAO: CRITICAL ERROR in findByCycle: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des participants", e);
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
        return participants;
    }

    public Participant findByCycleAndUser(int cycleId, int userId) {
        String sql = "SELECT * FROM participant WHERE cycle_id = ? AND utilisateur_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cycleId);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToParticipant(rs);
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

    public Participant findById(int id) {
        String sql = "SELECT * FROM participant WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToParticipant(rs);
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

    private Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        Participant p = new Participant();
        p.setId(rs.getInt("id"));

        int cycleId = rs.getInt("cycle_id");
        // Optimization: Do NOT load full cycle here if possible to avoid loops or heavy
        // load
        // But for simplicity we might load it, or just set ID if we had lazy loading.
        // Let's use DAO but be careful about circular dependencies if Cycle loads
        // Participants.
        // CycleDAO does NOT load participants by default, so it is safe.
        p.setCycle(cycleDAO.findById(cycleId));

        int userId = rs.getInt("utilisateur_id");
        p.setUtilisateur(utilisateurDAO.findById(userId));

        p.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        p.setPositionBeneficiaire(rs.getInt("position_beneficiaire"));

        return p;
    }

    public List<Participant> findByUtilisateur(int utilisateurId) {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT * FROM participant WHERE utilisateur_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, utilisateurId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                participants.add(mapResultSetToParticipant(rs));
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
        return participants;
    }
}
