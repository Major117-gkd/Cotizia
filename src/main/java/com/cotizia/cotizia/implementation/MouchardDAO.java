package com.cotizia.cotizia.implementation;

import com.cotizia.cotizia.models.Mouchard;
import com.cotizia.cotizia.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class MouchardDAO {

    public void log(String action, int utilisateurId) {
        String sql = "INSERT INTO mouchard (action, date_action, utilisateur_id) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, action);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, utilisateurId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Mouchard> findAll() {
        List<Mouchard> logs = new ArrayList<>();
        String sql = "SELECT m.*, u.nom, u.prenom FROM mouchard m JOIN utilisateur u ON m.utilisateur_id = u.id ORDER BY date_action DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Mouchard m = new Mouchard();
                m.setId(rs.getInt("id"));
                m.setAction(rs.getString("action"));
                java.sql.Timestamp ts = rs.getTimestamp("date_action");
                if (ts != null) {
                    m.setDateAction(ts.toLocalDateTime());
                }
                logs.add(m);
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
        return logs;
    }

    // Better implementation with proper mapping
    public List<String> findAllLogsWithUserNames() {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT m.action, m.date_action, u.nom, u.prenom FROM mouchard m JOIN utilisateur u ON m.utilisateur_id = u.id ORDER BY date_action DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String entry = rs.getTimestamp("date_action") + " - " + rs.getString("nom") + " "
                        + rs.getString("prenom") + " : " + rs.getString("action");
                logs.add(entry);
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
        return logs;
    }

    public List<String> getRecentLogs(int limit) {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT m.action, m.date_action, u.nom, u.prenom FROM mouchard m JOIN utilisateur u ON m.utilisateur_id = u.id ORDER BY date_action DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // Format date nicely
                String dateStr = "N/A";
                java.sql.Timestamp ts = rs.getTimestamp("date_action");
                if (ts != null) {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                            .ofPattern("dd/MM HH:mm");
                    dateStr = ts.toLocalDateTime().format(formatter);
                }

                String entry = "[" + dateStr + "] " + rs.getString("nom") + " "
                        + rs.getString("prenom") + " : " + rs.getString("action");
                logs.add(entry);
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
        return logs;
    }
}
