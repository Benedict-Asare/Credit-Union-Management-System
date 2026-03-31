package com.creditunion;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.service.MemberService;
import com.creditunion.service.AccountService;
import com.creditunion.service.TransactionService;
import com.creditunion.model.Member;
import com.creditunion.model.Account;
import com.creditunion.model.Transaction;

import java.util.List;

public class VerifyData {
    public static void main(String[] args) {
        System.out.println("=== Verifying Database Data ===\n");

        try {
            MemberService memberService = new MemberService();
            AccountService accountService = new AccountService();
            TransactionService transactionService = new TransactionService();

            // Show all members
            System.out.println("Members:");
            List<Member> members = memberService.getAllMembers();
            for (Member m : members) {
                System.out.println("  - " + m.getUsername() + " (" + m.getFullName() + ")");
            }
            System.out.println();

            // Show all accounts and transactions
            System.out.println("Accounts and Transactions:");
            for (Member m : members) {
                List<Account> accounts = accountService.getMemberAccounts(m.getMemberId());
                for (Account acc : accounts) {
                    System.out.println("  Account: " + acc.getAccountNumber() + " - Balance: $" + acc.getBalance());
                    List<Transaction> transactions = transactionService.getTransactionsByAccount(acc.getAccountNumber());
                    for (Transaction t : transactions) {
                        System.out.println("    - " + t.getTransactionDate() + " | " + t.getTransactionType() + " | $" + t.getAmount());
                    }
                }
            }
            System.out.println();

            // Show all transactions
            System.out.println("All Transactions:");
            List<Transaction> allTransactions = transactionService.getAllTransactions();
            for (Transaction t : allTransactions) {
                System.out.println("  - " + t.getTransactionDate() + " | " + t.getAccountNumber() + " | " +
                        t.getTransactionType() + " | $" + t.getAmount());
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}