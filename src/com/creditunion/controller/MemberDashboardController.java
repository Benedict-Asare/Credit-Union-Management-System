package com.creditunion.controller;

import com.creditunion.model.Member;
import com.creditunion.model.Account;
import com.creditunion.model.Transaction;
import com.creditunion.service.AccountService;
import com.creditunion.service.TransactionService;
import com.creditunion.utils.AlertUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberDashboardController {

    // Member Info Labels
    @FXML private Label welcomeLabel;
    @FXML private Label memberIdLabel;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label memberPhoneLabel;
    @FXML private Label memberAddressLabel;
    @FXML private Label memberSinceLabel;

    // Account Info
    @FXML private ComboBox<String> accountSelector;
    @FXML private Label accountNumberLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label accountStatusLabel;
    @FXML private Label accountCreatedLabel;

    // Deposit
    @FXML private TextField depositAmountField;
    @FXML private Label depositMessageLabel;

    // Transaction Table
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML private TableColumn<Transaction, LocalDateTime> dateColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;

    // Filters
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    // Statistics
    @FXML private Label totalDepositsLabel;
    @FXML private Label totalPayoutsLabel;
    @FXML private Label currentBalanceLabel;

    private Member currentMember;
    private Account currentAccount;
    private AccountService accountService;
    private TransactionService transactionService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ObservableList<Transaction> transactionData;

    @FXML
    public void initialize() {
        accountService = new AccountService();
        transactionService = new TransactionService();

        // Initialize observable list
        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);

        // Setup table columns
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Format transaction type column with color coding
        typeColumn.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    if ("Deposit".equals(type)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if ("Payout".equals(type)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #333;");
                    }
                }
            }
        });

        // Format amount column
        amountColumn.setCellFactory(col -> new TableCell<Transaction, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", amount));
                }
            }
        });

        // Format date column
        dateColumn.setCellFactory(col -> new TableCell<Transaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        // Account selector listener
        accountSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && currentMember != null) {
                loadAccountByNumber(newVal);
            }
        });

        System.out.println("MemberDashboardController initialized");
    }

    public void setMember(Object member) {
        this.currentMember = (Member) member;
        Platform.runLater(() -> {
            displayMemberInfo();
            loadMemberAccounts();
        });
    }

    private void displayMemberInfo() {
        welcomeLabel.setText("Welcome, " + currentMember.getFullName());
        memberIdLabel.setText(String.valueOf(currentMember.getMemberId()));
        memberNameLabel.setText(currentMember.getFullName());
        memberEmailLabel.setText(currentMember.getEmail());
        memberPhoneLabel.setText(currentMember.getPhone());
        memberAddressLabel.setText(currentMember.getAddress());
        if (currentMember.getCreatedDate() != null) {
            memberSinceLabel.setText(currentMember.getCreatedDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        }
    }

    private void loadMemberAccounts() {
        try {
            List<Account> accounts = accountService.getMemberAccounts(currentMember.getMemberId());
            if (accounts != null && !accounts.isEmpty()) {
                accountSelector.getItems().clear();
                for (Account acc : accounts) {
                    accountSelector.getItems().add(acc.getAccountNumber());
                }
                accountSelector.setValue(accounts.get(0).getAccountNumber());
                currentAccount = accounts.get(0);
                displayAccountInfo();
                loadTransactions();
                loadStatistics();
            } else {
                AlertUtils.showWarning("No Accounts", "No accounts found for this member.");
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Failed to load accounts: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // FIX: Catches RuntimeExceptions (e.g. NPE from null date_created) that
            // would otherwise propagate silently through Platform.runLater.
            AlertUtils.showError("Error", "Unexpected error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAccountByNumber(String accountNumber) {
        try {
            Account acc = accountService.getAccountByNumber(accountNumber);
            if (acc != null) {
                currentAccount = acc;
                displayAccountInfo();
                loadTransactions();
                loadStatistics();
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Failed to load account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayAccountInfo() {
        if (currentAccount != null) {
            accountNumberLabel.setText(currentAccount.getAccountNumber());
            accountTypeLabel.setText(currentAccount.getAccountType());
            balanceLabel.setText(String.format("$%,.2f", currentAccount.getBalance()));
            accountStatusLabel.setText(currentAccount.getStatus());
            if (currentAccount.getDateCreated() != null) {
                accountCreatedLabel.setText(currentAccount.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            }
        }
    }

    private void loadTransactions() {
        if (currentAccount == null) return;

        try {
            System.out.println("Loading transactions for account: " + currentAccount.getAccountNumber());
            List<Transaction> transactions;
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                java.sql.Date start = java.sql.Date.valueOf(startDatePicker.getValue());
                java.sql.Date end = java.sql.Date.valueOf(endDatePicker.getValue());
                transactions = transactionService.getTransactionsByDateRange(
                        currentAccount.getAccountNumber(), start, end);
            } else {
                transactions = transactionService.getTransactionsByAccount(
                        currentAccount.getAccountNumber());
            }

            System.out.println("Found " + (transactions != null ? transactions.size() : 0) + " transactions");
            
            // Update table directly on FX thread
            transactionData.clear();
            if (transactions != null && !transactions.isEmpty()) {
                transactionData.addAll(transactions);
            }
            transactionTable.refresh();
            
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Failed to load transactions: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.showError("Error", "Unexpected error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        if (currentAccount == null) return;

        try {
            List<Transaction> allTransactions = transactionService.getTransactionsByAccount(currentAccount.getAccountNumber());
            BigDecimal totalDeposits = BigDecimal.ZERO;
            BigDecimal totalPayouts = BigDecimal.ZERO;

            for (Transaction t : allTransactions) {
                if ("Deposit".equals(t.getTransactionType())) {
                    totalDeposits = totalDeposits.add(t.getAmount());
                } else if ("Payout".equals(t.getTransactionType())) {
                    totalPayouts = totalPayouts.add(t.getAmount());
                }
            }

            // Create final copies for lambda
            final BigDecimal finalTotalDeposits = totalDeposits;
            final BigDecimal finalTotalPayouts = totalPayouts;
            final BigDecimal finalBalance = currentAccount.getBalance();

            Platform.runLater(() -> {
                totalDepositsLabel.setText(String.format("$%,.2f", finalTotalDeposits));
                totalPayoutsLabel.setText(String.format("$%,.2f", finalTotalPayouts));
                currentBalanceLabel.setText(String.format("$%,.2f", finalBalance));
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeposit() {
        String amountStr = depositAmountField.getText().trim();
        if (amountStr.isEmpty()) {
            AlertUtils.showError("Error", "Please enter deposit amount");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtils.showError("Error", "Amount must be greater than zero");
                return;
            }

            boolean success = accountService.deposit(currentAccount.getAccountNumber(), amount, currentMember.getUsername());
            if (success) {
                AlertUtils.showSuccess("Deposit of $" + amount + " completed successfully!");
                depositAmountField.clear();
                depositMessageLabel.setText("✓ Deposit successful!");
                loadMemberAccounts(); // Refresh all data
            } else {
                AlertUtils.showError("Error", "Deposit failed");
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Error", "Please enter a valid amount");
        } catch (Exception e) {
            AlertUtils.showError("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadMemberAccounts();
        AlertUtils.showInfo("Refreshed", "Data has been refreshed");
    }

    @FXML
    private void handleApplyDateFilter() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            AlertUtils.showError("Error", "Please select both start and end dates");
            return;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            AlertUtils.showError("Error", "Start date cannot be after end date");
            return;
        }
        loadTransactions();
    }

    @FXML
    private void handleClearDateFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        loadTransactions();
    }

    @FXML
    private void handleLogout() {
        if (AlertUtils.showConfirmation("Logout", "Are you sure you want to logout?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Credit Union Management System - Login");
                stage.setMaximized(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}