package com.creditunion;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.service.MemberService;
import com.creditunion.service.AccountService;
import com.creditunion.service.TransactionService;
import com.creditunion.model.Member;
import com.creditunion.model.Account;
import com.creditunion.model.Transaction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class FullDiagnostics {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   CREDIT UNION SYSTEM DIAGNOSTIC");
        System.out.println("========================================\n");

        // Part 1: Direct Database Connection Test
        System.out.println("PART 1: DIRECT DATABASE CONNECTION TEST");
        System.out.println("----------------------------------------");
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✓ Database connection successful!");

            // Check Staff table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Staff");
            if (rs.next()) {
                System.out.println("  Staff table: " + rs.getInt("count") + " records");
            }

            // Check Members table
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Members");
            if (rs.next()) {
                System.out.println("  Members table: " + rs.getInt("count") + " records");
            }

            // Check Accounts table
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Accounts");
            if (rs.next()) {
                System.out.println("  Accounts table: " + rs.getInt("count") + " records");
            }

            // Check Transactions table
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Transactions");
            if (rs.next()) {
                System.out.println("  Transactions table: " + rs.getInt("count") + " records");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();

        // Part 2: Service Layer Test
        System.out.println("PART 2: SERVICE LAYER TEST");
        System.out.println("--------------------------");
        try {
            MemberService memberService = new MemberService();
            List<Member> members = memberService.getAllMembers();
            System.out.println("MemberService.getAllMembers() returned: " + members.size() + " members");
            for (Member m : members) {
                System.out.println("  - ID: " + m.getMemberId() + ", Username: " + m.getUsername() + ", Name: " + m.getFullName());
            }

            if (members.isEmpty()) {
                System.out.println("  ⚠️ WARNING: No members found! The database might be empty.");
            }

        } catch (Exception e) {
            System.err.println("✗ MemberService test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();

        // Part 3: Account Service Test
        System.out.println("PART 3: ACCOUNT SERVICE TEST");
        System.out.println("----------------------------");
        try {
            MemberService memberService = new MemberService();
            AccountService accountService = new AccountService();
            List<Member> members = memberService.getAllMembers();

            for (Member m : members) {
                List<Account> accounts = accountService.getMemberAccounts(m.getMemberId());
                System.out.println("Member: " + m.getUsername() + " has " + accounts.size() + " accounts");
                for (Account a : accounts) {
                    System.out.println("  - Account: " + a.getAccountNumber() + ", Type: " + a.getAccountType() +
                            ", Balance: $" + a.getBalance() + ", Status: " + a.getStatus());
                }
            }

        } catch (Exception e) {
            System.err.println("✗ AccountService test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();

        // Part 4: Transaction Service Test
        System.out.println("PART 4: TRANSACTION SERVICE TEST");
        System.out.println("---------------------------------");
        try {
            AccountService accountService = new AccountService();
            TransactionService transactionService = new TransactionService();
            MemberService memberService = new MemberService();
            List<Member> members = memberService.getAllMembers();

            for (Member m : members) {
                List<Account> accounts = accountService.getMemberAccounts(m.getMemberId());
                for (Account a : accounts) {
                    List<Transaction> transactions = transactionService.getTransactionsByAccount(a.getAccountNumber());
                    System.out.println("Account: " + a.getAccountNumber() + " has " + transactions.size() + " transactions");
                    for (Transaction t : transactions) {
                        System.out.println("  - " + t.getTransactionDate() + " | " + t.getTransactionType() +
                                " | $" + t.getAmount() + " | " + t.getDescription());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("✗ TransactionService test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();

        // Part 5: Direct SQL Query Test
        System.out.println("PART 5: DIRECT SQL QUERY TEST");
        System.out.println("-----------------------------");
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("Executing direct SQL queries:\n");

            // Get all members with their accounts and balances
            String sql = "SELECT m.member_id, m.username, m.full_name, " +
                    "a.account_number, a.account_type, a.balance, a.status " +
                    "FROM Members m " +
                    "LEFT JOIN Accounts a ON m.member_id = a.member_id " +
                    "ORDER BY m.member_id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("MEMBERS AND ACCOUNTS:");
            System.out.println("--------------------");
            while (rs.next()) {
                System.out.println("Member: " + rs.getString("username") + " (" + rs.getString("full_name") + ")");
                System.out.println("  Account: " + rs.getString("account_number"));
                System.out.println("  Type: " + rs.getString("account_type"));
                System.out.println("  Balance: $" + rs.getBigDecimal("balance"));
                System.out.println("  Status: " + rs.getString("status"));
                System.out.println();
            }
            rs.close();

            // Get all transactions
            sql = "SELECT t.*, a.account_number, m.username as member_name " +
                    "FROM Transactions t " +
                    "JOIN Accounts a ON t.account_number = a.account_number " +
                    "JOIN Members m ON a.member_id = m.member_id " +
                    "ORDER BY t.transaction_date DESC";

            rs = stmt.executeQuery(sql);
            System.out.println("ALL TRANSACTIONS:");
            System.out.println("-----------------");
            boolean hasTransactions = false;
            while (rs.next()) {
                hasTransactions = true;
                System.out.println("Date: " + rs.getTimestamp("transaction_date"));
                System.out.println("  Account: " + rs.getString("account_number") + " (" + rs.getString("member_name") + ")");
                System.out.println("  Type: " + rs.getString("transaction_type"));
                System.out.println("  Amount: $" + rs.getBigDecimal("amount"));
                System.out.println("  Description: " + rs.getString("description"));
                System.out.println("  Performed By: " + rs.getString("performed_by"));
                System.out.println();
            }

            if (!hasTransactions) {
                System.out.println("No transactions found in the database!");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.err.println("✗ Direct SQL query failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("   DIAGNOSTIC COMPLETE");
        System.out.println("========================================");
    }
}