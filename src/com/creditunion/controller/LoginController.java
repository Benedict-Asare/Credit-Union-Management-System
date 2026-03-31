package com.creditunion.controller;

import com.creditunion.service.AuthService;
import com.creditunion.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private Button loginButton;
    @FXML private Button cancelButton;

    private AuthService authService;

    @FXML
    public void initialize() {
        authService = new AuthService();
        userTypeCombo.getItems().addAll("Member", "Staff");
        userTypeCombo.setValue("Member");

        // Debug: Check if resources are accessible
        System.out.println("=== Resource Debug ===");
        URL loginFxml = getClass().getResource("/fxml/login.fxml");
        URL staffFxml = getClass().getResource("/fxml/staff_dashboard.fxml");
        URL memberFxml = getClass().getResource("/fxml/member_dashboard.fxml");

        System.out.println("login.fxml found: " + (loginFxml != null));
        System.out.println("staff_dashboard.fxml found: " + (staffFxml != null));
        System.out.println("member_dashboard.fxml found: " + (memberFxml != null));
        System.out.println("=====================");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String userType = userTypeCombo.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showError("Login Error", "Please enter username and password");
            return;
        }

        try {
            Object user = authService.login(username, password, userType.toLowerCase());

            if (user != null) {
                if ("Member".equals(userType)) {
                    loadMemberDashboard(user);
                } else {
                    loadStaffDashboard(user);
                }
            } else {
                AlertUtils.showError("Login Failed", "Invalid username or password");
            }
        } catch (SQLException e) {
            AlertUtils.showError("Database Error", "Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.showError("Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMemberDashboard(Object user) {
        try {
            System.out.println("Loading member dashboard...");
            URL fxmlUrl = getClass().getResource("/fxml/member_dashboard.fxml");
            if (fxmlUrl == null) {
                AlertUtils.showError("Error", "Cannot find member_dashboard.fxml file!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            MemberDashboardController controller = loader.getController();
            controller.setMember(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Member Dashboard");
            stage.setMaximized(true);
            System.out.println("Member dashboard loaded successfully");

        } catch (IOException e) {
            AlertUtils.showError("File Error", "Cannot load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStaffDashboard(Object user) {
        try {
            System.out.println("Loading staff dashboard...");
            URL fxmlUrl = getClass().getResource("/fxml/staff_dashboard.fxml");
            if (fxmlUrl == null) {
                AlertUtils.showError("Error", "Cannot find staff_dashboard.fxml file!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            StaffDashboardController controller = loader.getController();
            controller.setStaff(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Staff Dashboard");
            stage.setMaximized(true);
            System.out.println("Staff dashboard loaded successfully");

        } catch (IOException e) {
            AlertUtils.showError("File Error", "Cannot load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}