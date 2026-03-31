package com.creditunion.service;

import com.creditunion.dao.TransactionDAO;
import com.creditunion.model.Transaction;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private TransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) throws SQLException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        return transactionDAO.getTransactionsByAccountNumber(accountNumber);
    }

    public List<Transaction> getTransactionsByDateRange(String accountNumber, Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return transactionDAO.getTransactionsByDateRange(accountNumber, startDate, endDate);
    }

    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return transactionDAO.getTransactionsByDateRange(startDate, endDate);
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.getAllTransactions();
    }

    public boolean recordTransaction(Transaction transaction) throws SQLException {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        return transactionDAO.createTransaction(transaction);
    }
}