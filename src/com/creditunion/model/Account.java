package com.creditunion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private int accountId;
    private int memberId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime dateCreated;
    private String status;

    public Account() {}

    public Account(int memberId, String accountNumber, String accountType, BigDecimal balance) {
        this.memberId = memberId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}