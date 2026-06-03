package org.example.admissionsecretary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class ApplicantDAO {
    public void add(Applicant a) throws SQLException {
        String sql = "INSERT INTO Applicant (full_name, email, list_id, ege_math, ege_russian, " +
                "ege_subject, achievements_score, portfolio_score, is_target, is_priority) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, a.getFullName());
        stmt.setString(2, a.getEmail());
        stmt.setInt(3, a.getListId());
        stmt.setInt(4, a.getEgeMath());
        stmt.setInt(5, a.getEgeRussian());
        stmt.setInt(6, a.getEgeSubject());
        stmt.setInt(7, a.getAchievementsScore());
        stmt.setInt(8, a.getPortfolioScore());
        stmt.setBoolean(9, a.isTarget());
        stmt.setBoolean(10, a.isPriority());
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            a.setId(keys.getInt(1));
        }
    }

    public List<Applicant> getByListId(int listId) throws SQLException {
        List<Applicant> applicants = new ArrayList<>();
        String sql = "SELECT * FROM Applicant WHERE list_id = ? " +
                "ORDER BY is_target DESC, is_priority DESC, total_score DESC";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, listId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            applicants.add(mapRow(rs));
        }
        return applicants;
    }

    public List<Applicant> getAll() throws SQLException {
        List<Applicant> applicants = new ArrayList<>();
        String sql = "SELECT * FROM Applicant ORDER BY is_target DESC, is_priority DESC, total_score DESC";
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            applicants.add(mapRow(rs));
        }
        return applicants;
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE Applicant SET status = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Applicant WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Applicant getById(int id) throws SQLException {
        String sql = "SELECT * FROM Applicant WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    private Applicant mapRow(ResultSet rs) throws SQLException {
        Applicant a = new Applicant();
        a.setId(rs.getInt("id"));
        a.setFullName(rs.getString("full_name"));
        a.setEmail(rs.getString("email"));
        a.setListId(rs.getInt("list_id"));
        a.setEgeMath(rs.getInt("ege_math"));
        a.setEgeRussian(rs.getInt("ege_russian"));
        a.setEgeSubject(rs.getInt("ege_subject"));
        a.setAchievementsScore(rs.getInt("achievements_score"));
        a.setPortfolioScore(rs.getInt("portfolio_score"));
        a.setTotalScore(rs.getInt("total_score"));
        a.setTarget(rs.getBoolean("is_target"));
        a.setPriority(rs.getBoolean("is_priority"));
        a.setStatus(rs.getString("status"));
        return a;
    }
}
