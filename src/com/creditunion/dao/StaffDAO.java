package com.creditunion.dao;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.model.Staff;

import java.sql.*;

public class StaffDAO {

    public Staff getStaffByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffId(rs.getInt("staff_id"));
                staff.setUsername(rs.getString("username"));
                staff.setPassword(rs.getString("password")); // Plain text password
                staff.setRole(rs.getString("role"));
                staff.setFullName(rs.getString("full_name"));
                staff.setCreatedDate(toLocalDateTimeSafe(rs.getTimestamp("created_date")));
                return staff;
            }
            return null;
        }
    }

    private java.time.LocalDateTime toLocalDateTimeSafe(java.sql.Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public boolean validateStaffCredentials(String username, String password) throws SQLException {
        Staff staff = getStaffByUsername(username);
        if (staff != null) {
            // Simple direct password comparison (no BCrypt)
            return staff.getPassword().equals(password);
        }
        return false;
    }

    public boolean createStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO Staff (username, password, role, full_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, staff.getUsername());
            // Store password as plain text (temporary)
            pstmt.setString(2, staff.getPassword());
            pstmt.setString(3, staff.getRole());
            pstmt.setString(4, staff.getFullName());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    staff.setStaffId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }
}