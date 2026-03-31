package com.creditunion;

import com.creditunion.config.DatabaseConfig;
import com.creditunion.utils.AlertUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;

public class Main extends Application {

    private static final String APP_VERSION = "1.0.0";
    private static final String APP_NAME = "Credit Union Management System";

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection first
            System.out.println("=== Credit Union Management System ===");
            System.out.println("Testing database connection to CreditUnionSystemDB...");
            DatabaseConfig.testConnection();
            System.out.println("================================\n");

            // Show splash screen
            showSplashScreen();

            // Load login screen after splash
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Show splash for 2 seconds
                    Platform.runLater(() -> {
                        try {
                            loadLoginScreen(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertUtils.showError("Startup Error", "Failed to load login screen: " + e.getMessage());
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            AlertUtils.showError("Startup Error", "Failed to start application: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void showSplashScreen() {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.initStyle(StageStyle.TRANSPARENT);

        VBox splashRoot = new VBox(10);
        splashRoot.setAlignment(Pos.CENTER);
        splashRoot.setStyle("-fx-background-color: #0a5c8e; -fx-padding: 30px; -fx-background-radius: 15px;");

        Label titleLabel = new Label(APP_NAME);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label versionLabel = new Label("Version " + APP_VERSION);
        versionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.setMaxWidth(200);
        progressBar.setStyle("-fx-accent: #27ae60;");

        splashRoot.getChildren().addAll(titleLabel, versionLabel, progressBar, loadingLabel);

        Scene splashScene = new Scene(splashRoot, 500, 300);
        splashScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        splashStage.setScene(splashScene);
        splashStage.show();

        // Close splash after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(splashStage::close);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadLoginScreen(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        System.out.println("✓ Login screen loaded successfully");
    }

    public static void main(String[] args) {
        System.out.println("=== Credit Union Management System v" + APP_VERSION + " ===");
        System.out.println("Starting application...");

        launch(args);

        // Close database connection when app exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.closeConnection();
            System.out.println("Application shutdown complete");
        }));
    }
}