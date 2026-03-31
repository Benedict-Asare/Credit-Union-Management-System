package com.creditunion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;  // This should be LocalDateTime
    private String performedBy;

    public Transaction() {}

    public Transaction(String accountNumber, String transactionType, BigDecimal amount,
                       String performedBy) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.performedBy = performedBy;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
}