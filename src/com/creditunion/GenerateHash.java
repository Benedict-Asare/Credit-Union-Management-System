package com.creditunion;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        System.out.println("=== Generate BCrypt Hashes for Login ===\n");

        // Generate hash for admin (password: admin123)
        String adminPassword = "admin123";
        String adminHash = BCrypt.hashpw(adminPassword, BCrypt.gensalt());
        System.out.println("ADMIN LOGIN:");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("Hash to insert: " + adminHash);
        System.out.println("Verification: " + BCrypt.checkpw(adminPassword, adminHash));
        System.out.println();

        // Generate hash for member (password: member123)
        String memberPassword = "member123";
        String memberHash = BCrypt.hashpw(memberPassword, BCrypt.gensalt());
        System.out.println("MEMBER LOGIN:");
        System.out.println("Username: john");
        System.out.println("Password: member123");
        System.out.println("Hash to insert: " + memberHash);
        System.out.println("Verification: " + BCrypt.checkpw(memberPassword, memberHash));
        System.out.println();

        System.out.println("=== COPY THESE HASHES INTO YOUR SQL SCRIPT ===");
        System.out.println("Admin Hash: " + adminHash);
        System.out.println("Member Hash: " + memberHash);
    }
}