package com.creditunion.dao;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public boolean createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO Accounts (member_id, account_number, account_type, balance) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, account.getMemberId());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setString(3, account.getAccountType());
            pstmt.setBigDecimal(4, account.getBalance());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setAccountId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setMemberId(rs.getInt("member_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setDateCreated(toLocalDateTimeSafe(rs.getTimestamp("date_created")));
                account.setStatus(rs.getString("status"));
                return account;
            }
            return null;
        }
    }

    public List<Account> getAccountsByMemberId(int memberId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts WHERE member_id = ? ORDER BY date_created DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setMemberId(rs.getInt("member_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setDateCreated(toLocalDateTimeSafe(rs.getTimestamp("date_created")));
                account.setStatus(rs.getString("status"));
                accounts.add(account);
            }
        }
        return accounts;
    }

    private java.time.LocalDateTime toLocalDateTimeSafe(java.sql.Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public boolean updateBalance(String accountNumber, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE Accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, newBalance);
            pstmt.setString(2, accountNumber);

            return pstmt.executeUpdate() > 0;
        }
    }

    public String generateAccountNumber() throws SQLException {
        return "ACC" + System.currentTimeMillis();
    }
}