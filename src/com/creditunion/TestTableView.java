package com.creditunion;

import com.creditunion.model.Transaction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestTableView extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<Transaction, BigDecimal> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, LocalDateTime> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        TableColumn<Transaction, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(typeColumn, amountColumn, dateColumn, descColumn);

        // Add sample data
        ObservableList<Transaction> data = FXCollections.observableArrayList();
        Transaction t1 = new Transaction();
        t1.setTransactionType("Deposit");
        t1.setAmount(new BigDecimal("1000.00"));
        t1.setTransactionDate(LocalDateTime.now());
        t1.setDescription("Test Deposit");
        data.add(t1);

        table.setItems(data);

        VBox root = new VBox(table);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Table View Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Table items: " + table.getItems().size());
    }

    public static void main(String[] args) {
        launch(args);
    }
}