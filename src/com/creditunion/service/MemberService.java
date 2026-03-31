package com.creditunion.service;

import com.creditunion.dao.AccountDAO;
import com.creditunion.dao.MemberDAO;
import com.creditunion.model.Account;
import com.creditunion.model.Member;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MemberService {
    private MemberDAO memberDAO;
    private AccountDAO accountDAO;

    public MemberService() {
        this.memberDAO = new MemberDAO();
        this.accountDAO = new AccountDAO();
    }

    public Member createMember(Member member) throws SQLException {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (member.getUsername() == null || member.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (member.getPassword() == null || member.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Check if username already exists
        Member existing = memberDAO.getMemberByUsername(member.getUsername());
        if (existing != null) {
            throw new SQLException("Username already exists");
        }

        boolean created = memberDAO.createMember(member);
        if (created) {
            // Automatically create a savings account for the new member
            Account savingsAccount = new Account();
            savingsAccount.setMemberId(member.getMemberId());
            savingsAccount.setAccountNumber(accountDAO.generateAccountNumber());
            savingsAccount.setAccountType("Savings");
            savingsAccount.setBalance(BigDecimal.ZERO);
            savingsAccount.setStatus("Active");
            accountDAO.createAccount(savingsAccount);
            return member;
        }
        return null;
    }

    public List<Member> getAllMembers() throws SQLException {
        return memberDAO.getAllMembers();
    }

    public Member getMemberByUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return memberDAO.getMemberByUsername(username);
    }

    public Member getMemberById(int memberId) throws SQLException {
        List<Member> members = getAllMembers();
        for (Member member : members) {
            if (member.getMemberId() == memberId) {
                return member;
            }
        }
        return null;
    }

    public boolean updateMember(Member member) throws SQLException {
        // This would require implementing update in MemberDAO
        // For now, return false
        return false;
    }

    public int getTotalMembers() throws SQLException {
        return getAllMembers().size();
    }
    public void debugPrintAllMembers() {
        try {
            List<Member> members = getAllMembers();
            System.out.println("=== DEBUG: Total Members Found: " + members.size());
            for (Member m : members) {
                System.out.println("  Member: " + m.getUsername() + " - " + m.getFullName());
            }
        } catch (SQLException e) {
            System.err.println("Debug error: " + e.getMessage());
        }
    }
}