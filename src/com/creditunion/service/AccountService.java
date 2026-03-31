package com.creditunion.service;

import com.creditunion.dao.AccountDAO;
import com.creditunion.dao.TransactionDAO;
import com.creditunion.model.Account;
import com.creditunion.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AccountService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public boolean deposit(String accountNumber, BigDecimal amount, String performedBy) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new SQLException("Account not found");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        boolean updated = accountDAO.updateBalance(accountNumber, newBalance);

        if (updated) {
            Transaction transaction = new Transaction(accountNumber, "Deposit", amount, performedBy);
            transaction.setDescription("Deposit of " + amount);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }

    public boolean payout(String accountNumber, String performedBy) throws SQLException {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new SQLException("Account not found");
        }

        BigDecimal amount = account.getBalance();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("No funds to payout");
        }

        boolean updated = accountDAO.updateBalance(accountNumber, BigDecimal.ZERO);

        if (updated) {
            Transaction transaction = new Transaction(accountNumber, "Payout", amount, performedBy);
            transaction.setDescription("Full payout of " + amount);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    public List<Account> getMemberAccounts(int memberId) throws SQLException {
        return accountDAO.getAccountsByMemberId(memberId);
    }
}

