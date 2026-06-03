package org.example.admissionsecretary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionListDAO {
    public List<AdmissionList> getAll() throws SQLException {
        List<AdmissionList> lists = new ArrayList<>();
        String sql = "SELECT * FROM AdmissionList";
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            AdmissionList list = new AdmissionList();
            list.setId(rs.getInt("id"));
            list.setName(rs.getString("name"));
            list.setSpecialityName(rs.getString("speciality_name"));
            list.setTotalPlaces(rs.getInt("total_places"));
            list.setTargetPlaces(rs.getInt("target_places"));
            list.setPriorityPlaces(rs.getInt("priority_places"));
            lists.add(list);
        }
        return lists;
    }

    public void add(AdmissionList list) throws SQLException {
        String sql = "INSERT INTO AdmissionList (name, speciality_name, total_places, target_places, priority_places) " +
                "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, list.getName());
        stmt.setString(2, list.getSpecialityName());
        stmt.setInt(3, list.getTotalPlaces());
        stmt.setInt(4, list.getTargetPlaces());
        stmt.setInt(5, list.getPriorityPlaces());
        stmt.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM AdmissionList WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
}
