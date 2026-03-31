package com.creditunion.controller;

import com.creditunion.model.Staff;
import com.creditunion.model.Member;
import com.creditunion.model.Account;
import com.creditunion.model.Transaction;
import com.creditunion.service.MemberService;
import com.creditunion.service.AccountService;
import com.creditunion.service.TransactionService;
import com.creditunion.utils.AlertUtils;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TabPane mainTabPane;

    // Member Registration Tab
    @FXML private TextField regFullName;
    @FXML private TextField regPhone;
    @FXML private TextField regEmail;
    @FXML private TextField regAddress;
    @FXML private TextField regUsername;
    @FXML private PasswordField regPassword;
    @FXML private ComboBox<String> regAccountType;

    // Deposit Tab
    @FXML private TextField depositAccountNumber;
    @FXML private TextField depositAmount;
    @FXML private Label depositBalanceLabel;

    // Payout Tab
    @FXML private TextField payoutAccountNumber;
    @FXML private Label payoutBalanceLabel;
    @FXML private Label payoutMemberNameLabel;

    // View Accounts Tab
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, String> memberNameColumn;
    @FXML private TableColumn<Member, String> memberUsernameColumn;
    @FXML private TableColumn<Member, String> memberPhoneColumn;
    @FXML private TableColumn<Member, String> memberEmailColumn;

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> accountNumberColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, BigDecimal> balanceColumn;

    // Transactions Tab
    @FXML private TextField transactionAccountNumber;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> transTypeColumn;
    @FXML private TableColumn<Transaction, BigDecimal> transAmountColumn;
    // FIX: Was TableColumn<Transaction, String> but PropertyValueFactory("transactionDate")
    // returns a LocalDateTime — the type mismatch caused a ClassCastException at
    // runtime so the date column never displayed anything.
    @FXML private TableColumn<Transaction, java.time.LocalDateTime> transDateColumn;
    @FXML private TableColumn<Transaction, String> transDescriptionColumn;
    @FXML private TableColumn<Transaction, String> transPerformedByColumn;
    @FXML private TableColumn<Transaction, String> transAccountNumberColumn;

    private Staff currentStaff;
    private MemberService memberService;
    private AccountService accountService;
    private TransactionService transactionService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        memberService = new MemberService();
        accountService = new AccountService();
        transactionService = new TransactionService();

        // Debug: Print all members
        try {
            List<Member> members = memberService.getAllMembers();
            System.out.println("=== STAFF DASHBOARD DEBUG ===");
            System.out.println("Total Members in DB: " + members.size());
            for (Member m : members) {
                System.out.println("  - " + m.getUsername() + ": " + m.getFullName());
            }
            if (members.isEmpty()) {
                System.out.println("WARNING: No members found in database!");
            }
        } catch (SQLException e) {
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
        }

        // Setup account type combo box
        regAccountType.getItems().addAll("Savings", "Fixed Deposit", "Current");
        regAccountType.setValue("Savings");

        // Setup member table columns
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        memberUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        memberPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        memberEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Setup account table columns
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // Format currency for balance column
        balanceColumn.setCellFactory(column -> new TableCell<Account, BigDecimal>() {
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

        // Setup transaction table columns
        transTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        transAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        transDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        transPerformedByColumn.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        transAccountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // Format transaction type column with color coding
        transTypeColumn.setCellFactory(col -> new TableCell<Transaction, String>() {
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

        // Format amount column in transaction table
        transAmountColumn.setCellFactory(column -> new TableCell<Transaction, BigDecimal>() {
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

        // FIX: Cell factory now uses LocalDateTime (matching the corrected column
        // type) and formats the date with dateFormatter instead of calling setText(date)
        // on a raw String that was actually a LocalDateTime object at runtime.
        transDateColumn.setCellFactory(column -> new TableCell<Transaction, java.time.LocalDateTime>() {
            @Override
            protected void updateItem(java.time.LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });

        // Load initial data
        loadMembers();

        // Setup member table selection listener
        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadMemberAccounts(newSelection.getMemberId());
            }
        });
    }

    public void setStaff(Object staff) {
        this.currentStaff = (Staff) staff;
        welcomeLabel.setText("Welcome, " + currentStaff.getFullName() + " (" + currentStaff.getRole() + ")");
    }

    @FXML
    private void handleRegisterMember() {
        // Validate input
        if (regFullName.getText().isEmpty() || regPhone.getText().isEmpty() ||
                regEmail.getText().isEmpty() || regAddress.getText().isEmpty() ||
                regUsername.getText().isEmpty() || regPassword.getText().isEmpty()) {
            AlertUtils.showError("Error", "Please fill all fields");
            return;
        }

        // Validate email format
        if (!regEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtils.showError("Error", "Please enter a valid email address");
            return;
        }

        // Validate phone format (basic validation)
        if (!regPhone.getText().matches("^[0-9]{10,15}$")) {
            AlertUtils.showError("Error", "Please enter a valid phone number (10-15 digits)");
            return;
        }

        Member member = new Member();
        member.setFullName(regFullName.getText());
        member.setPhone(regPhone.getText());
        member.setEmail(regEmail.getText());
        member.setAddress(regAddress.getText());
        member.setUsername(regUsername.getText());
        member.setPassword(regPassword.getText());

        try {
            Member created = memberService.createMember(member);
            if (created != null) {
                AlertUtils.showInfo("Success", "Member registered successfully!\n\n" +
                        "Username: " + created.getUsername() + "\n" +
                        "Member ID: " + created.getMemberId() + "\n" +
                        "A Savings Account has been automatically created.");
                clearRegistrationForm();
                loadMembers();
                mainTabPane.getSelectionModel().select(2);
            } else {
                AlertUtils.showError("Error", "Failed to register member");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate") || e.getMessage().contains("UNIQUE")) {
                AlertUtils.showError("Error", "Username already exists. Please choose another.");
            } else {
                AlertUtils.showError("Error", "Database error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCheckAccount() {
        String accountNumber = depositAccountNumber.getText().trim();
        if (accountNumber.isEmpty()) {
            AlertUtils.showError("Error", "Please enter account number");
            return;
        }

        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            if (account != null) {
                depositBalanceLabel.setText(String.format("Current Balance: $%,.2f", account.getBalance()));
                AlertUtils.showInfo("Account Found", "Account: " + accountNumber +
                        "\nType: " + account.getAccountType() +
                        "\nBalance: $" + String.format("%,.2f", account.getBalance()));
            } else {
                depositBalanceLabel.setText("Account not found");
                AlertUtils.showError("Error", "Account not found");
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeposit() {
        String accountNumber = depositAccountNumber.getText().trim();
        String amountStr = depositAmount.getText().trim();

        if (accountNumber.isEmpty() || amountStr.isEmpty()) {
            AlertUtils.showError("Error", "Please enter account number and deposit amount");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtils.showError("Error", "Amount must be greater than zero");
                return;
            }

            boolean success = accountService.deposit(accountNumber, amount, currentStaff.getUsername());

            if (success) {
                AlertUtils.showInfo("Success", String.format("Deposit of $%,.2f completed successfully", amount));
                depositAccountNumber.clear();
                depositAmount.clear();
                depositBalanceLabel.setText("");
                loadMembers();
                // Refresh transactions if account number matches
                if (transactionAccountNumber.getText().trim().equals(accountNumber)) {
                    handleViewTransactions();
                }
            } else {
                AlertUtils.showError("Error", "Deposit failed");
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Error", "Please enter a valid amount");
        } catch (IllegalArgumentException e) {
            AlertUtils.showError("Error", e.getMessage());
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckPayoutAccount() {
        String accountNumber = payoutAccountNumber.getText().trim();
        if (accountNumber.isEmpty()) {
            AlertUtils.showError("Error", "Please enter account number");
            return;
        }

        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            if (account != null) {
                payoutBalanceLabel.setText(String.format("$%,.2f", account.getBalance()));

                Member member = memberService.getMemberById(account.getMemberId());
                if (member != null) {
                    payoutMemberNameLabel.setText(member.getFullName());
                } else {
                    payoutMemberNameLabel.setText("Unknown Member");
                }

                if (account.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                    AlertUtils.showWarning("Warning", "Account has no funds to payout");
                }
            } else {
                payoutBalanceLabel.setText("Account not found");
                payoutMemberNameLabel.setText("");
                AlertUtils.showError("Error", "Account not found");
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePayout() {
        String accountNumber = payoutAccountNumber.getText().trim();

        if (accountNumber.isEmpty()) {
            AlertUtils.showError("Error", "Please enter account number");
            return;
        }

        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            if (account == null) {
                AlertUtils.showError("Error", "Account not found");
                return;
            }

            if (account.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtils.showWarning("Warning", "Account has no funds to payout");
                return;
            }

            boolean confirmed = AlertUtils.showConfirmation("Confirm Payout",
                    "Are you sure you want to payout $" + String.format("%,.2f", account.getBalance()) +
                            " from account " + accountNumber + "?\n\nThis action cannot be undone.");

            if (confirmed) {
                boolean success = accountService.payout(accountNumber, currentStaff.getUsername());
                if (success) {
                    AlertUtils.showInfo("Success", String.format("Payout of $%,.2f completed successfully", account.getBalance()));
                    payoutAccountNumber.clear();
                    payoutBalanceLabel.setText("");
                    payoutMemberNameLabel.setText("");
                    loadMembers();
                } else {
                    AlertUtils.showError("Error", "Payout failed");
                }
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            AlertUtils.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleViewTransactions() {
        String accountNumber = transactionAccountNumber.getText().trim();

        if (accountNumber.isEmpty()) {
            AlertUtils.showError("Error", "Please enter account number");
            return;
        }

        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccount(accountNumber);
            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
            transactionTable.setItems(transactionList);

            if (transactions.isEmpty()) {
                AlertUtils.showInfo("Info", "No transactions found for account " + accountNumber);
            } else {
                System.out.println("Found " + transactions.size() + " transactions for account " + accountNumber);
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Failed to load transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
            transactionTable.setItems(transactionList);

            if (transactions.isEmpty()) {
                AlertUtils.showInfo("Info", "No transactions found in the system");
            } else {
                AlertUtils.showInfo("Info", "Found " + transactions.size() + " transactions in the system");
                System.out.println("Total transactions in system: " + transactions.size());
                for (Transaction t : transactions) {
                    System.out.println("  - " + t.getAccountNumber() + ": " + t.getTransactionType() + " $" + t.getAmount());
                }
            }
        } catch (SQLException e) {
            AlertUtils.showError("Error", "Failed to load all transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefreshMembers() {
        loadMembers();
        AlertUtils.showInfo("Refreshed", "Data has been refreshed");
    }

    @FXML
    private void handleSearchMember() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Member");
        dialog.setHeaderText("Search by Username");
        dialog.setContentText("Enter username:");

        dialog.showAndWait().ifPresent(username -> {
            try {
                Member member = memberService.getMemberByUsername(username);
                if (member != null) {
                    memberTable.getSelectionModel().select(member);
                    memberTable.scrollTo(member);
                    AlertUtils.showInfo("Member Found", "Member: " + member.getFullName() +
                            "\nUsername: " + member.getUsername() +
                            "\nPhone: " + member.getPhone() +
                            "\nEmail: " + member.getEmail());
                } else {
                    AlertUtils.showError("Not Found", "No member found with username: " + username);
                }
            } catch (SQLException e) {
                AlertUtils.showError("Error", "Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAbout() {
        AlertUtils.showInfo("About",
                "Credit Union Management System\nVersion 1.0.0\n\n" +
                        "A comprehensive financial management solution\n" +
                        "for credit unions and financial institutions.\n\n" +
                        "© 2024 Credit Union Management System\n" +
                        "All Rights Reserved.");
    }

    private void loadMembers() {
        try {
            System.out.println("Loading members from database...");
            List<Member> members = memberService.getAllMembers();
            System.out.println("Found " + members.size() + " members in database");
            
            if (members.isEmpty()) {
                System.out.println("WARNING: No members found in database!");
                AlertUtils.showWarning("No Data", "No members found in the database. Please run the SQL script to populate test data.");
            }
            
            ObservableList<Member> memberList = FXCollections.observableArrayList(members);
            memberTable.setItems(memberList);
        } catch (Exception e) {
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Error", "Failed to load members: " + e.getMessage());
        }
    }

    private void loadMemberAccounts(int memberId) {
        try {
            System.out.println("Loading accounts for member ID: " + memberId);
            List<Account> accounts = accountService.getMemberAccounts(memberId);
            System.out.println("Found " + accounts.size() + " accounts");
            
            if (accounts.isEmpty()) {
                System.out.println("WARNING: No accounts found for member!");
            }
            
            ObservableList<Account> accountList = FXCollections.observableArrayList(accounts);
            accountTable.setItems(accountList);
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Error", "Failed to load accounts: " + e.getMessage());
        }
    }

    private void clearRegistrationForm() {
        regFullName.clear();
        regPhone.clear();
        regEmail.clear();
        regAddress.clear();
        regUsername.clear();
        regPassword.clear();
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtils.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirmed) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Credit Union Management System - Login");
                stage.setMaximized(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}