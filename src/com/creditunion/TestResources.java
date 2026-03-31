package com.creditunion;

import java.net.URL;

public class TestResources {
    public static void main(String[] args) {
        System.out.println("=== Testing Resource Loading ===\n");

        String[] resources = {
                "/fxml/login.fxml",
                "/fxml/staff_dashboard.fxml",
                "/fxml/member_dashboard.fxml",
                "/styles/styles.css"
        };

        for (String resource : resources) {
            URL url = TestResources.class.getResource(resource);
            if (url != null) {
                System.out.println("✓ FOUND: " + resource);
                System.out.println("  Path: " + url.getPath());
            } else {
                System.out.println("✗ NOT FOUND: " + resource);
            }
        }

        System.out.println("\nIf resources are not found, check:");
        System.out.println("1. resources folder is at the same level as src");
        System.out.println("2. resources folder is marked as Resources Root (purple)");
        System.out.println("3. FXML files are in resources/fxml/ folder");
        System.out.println("4. CSS file is in resources/styles/ folder");
    }
}