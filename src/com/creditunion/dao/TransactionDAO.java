package com.creditunion.dao;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO Transactions (account_number, transaction_type, amount, description, performed_by, transaction_date) VALUES (?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, transaction.getAccountNumber());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());
            pstmt.setString(5, transaction.getPerformedBy());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE account_number = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountNumber(rs.getString("account_number"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTransactionDate(toLocalDateTimeSafe(rs.getTimestamp("transaction_date")));
                transaction.setPerformedBy(rs.getString("performed_by"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    // Method for filtering by date range with account number
    public List<Transaction> getTransactionsByDateRange(String accountNumber, Date startDate, Date endDate) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE account_number = ? AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountNumber(rs.getString("account_number"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTransactionDate(toLocalDateTimeSafe(rs.getTimestamp("transaction_date")));
                transaction.setPerformedBy(rs.getString("performed_by"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    // Method for filtering by date range without account number (all accounts)
    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountNumber(rs.getString("account_number"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTransactionDate(toLocalDateTimeSafe(rs.getTimestamp("transaction_date")));
                transaction.setPerformedBy(rs.getString("performed_by"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    private java.time.LocalDateTime toLocalDateTimeSafe(java.sql.Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public BigDecimal getTotalDepositsByAccount(String accountNumber) throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM Transactions WHERE account_number = ? AND transaction_type = 'Deposit'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountNumber(rs.getString("account_number"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTransactionDate(toLocalDateTimeSafe(rs.getTimestamp("transaction_date")));
                transaction.setPerformedBy(rs.getString("performed_by"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }
}