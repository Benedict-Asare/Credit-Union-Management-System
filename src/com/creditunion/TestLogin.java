package com.creditunion;

import com.creditunion.service.AuthService;

public class TestLogin {
    public static void main(String[] args) {
        System.out.println("=== Testing Login System ===\n");

        try {
            AuthService authService = new AuthService();

            // Test Staff Login
            System.out.println("Testing Staff Login...");
            Object staff = authService.login("admin", "admin123", "staff");
            if (staff != null) {
                System.out.println("✓ Staff login SUCCESSFUL!");
                System.out.println("  Staff: " + staff.getClass().getSimpleName());
            } else {
                System.out.println("✗ Staff login FAILED!");
            }

            // Test Member Login
            System.out.println("\nTesting Member Login...");
            Object member = authService.login("john", "member123", "member");
            if (member != null) {
                System.out.println("✓ Member login SUCCESSFUL!");
                System.out.println("  Member: " + member.getClass().getSimpleName());
            } else {
                System.out.println("✗ Member login FAILED!");
            }

            // Test Wrong Password
            System.out.println("\nTesting Wrong Password...");
            Object wrong = authService.login("admin", "wrongpassword", "staff");
            if (wrong == null) {
                System.out.println("✓ Wrong password correctly rejected");
            } else {
                System.out.println("✗ Wrong password was accepted!");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}