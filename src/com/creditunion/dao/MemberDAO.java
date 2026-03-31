package com.creditunion.dao;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean createMember(Member member) throws SQLException {
        String sql = "INSERT INTO Members (full_name, phone, email, address, username, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, member.getFullName());
            pstmt.setString(2, member.getPhone());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getAddress());
            pstmt.setString(5, member.getUsername());

            // Store password as plain text (temporary - remove BCrypt)
            pstmt.setString(6, member.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    member.setMemberId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public Member getMemberByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Members WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("member_id"));
                member.setFullName(rs.getString("full_name"));
                member.setPhone(rs.getString("phone"));
                member.setEmail(rs.getString("email"));
                member.setAddress(rs.getString("address"));
                member.setUsername(rs.getString("username"));
                member.setPassword(rs.getString("password")); // Plain text password
                member.setCreatedDate(toLocalDateTimeSafe(rs.getTimestamp("created_date")));
                return member;
            }
            return null;
        }
    }

    public boolean validateMemberCredentials(String username, String password) throws SQLException {
        Member member = getMemberByUsername(username);
        if (member != null) {
            // Simple direct password comparison (no BCrypt)
            return member.getPassword().equals(password);
        }
        return false;
    }

    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Members ORDER BY member_id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("member_id"));
                member.setFullName(rs.getString("full_name"));
                member.setPhone(rs.getString("phone"));
                member.setEmail(rs.getString("email"));
                member.setAddress(rs.getString("address"));
                member.setUsername(rs.getString("username"));
                member.setPassword(rs.getString("password"));
                member.setCreatedDate(toLocalDateTimeSafe(rs.getTimestamp("created_date")));
                members.add(member);
            }
        }
        return members;
    }

    private java.time.LocalDateTime toLocalDateTimeSafe(java.sql.Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}