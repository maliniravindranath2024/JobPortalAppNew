package com.application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAdminController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField roleField;

    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        // Initialize roleField with default "admin" and make it uneditable
        roleField.setText("admin");
        roleField.setEditable(false);

        // Add event handler to saveButton
        //saveButton.setOnAction(event -> saveAdminDetails());
    }

    @FXML
    private void handleSave() {
        saveAdminDetails(); // Call your existing method to save the admin details
    }

    private void saveAdminDetails() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleField.getText().trim(); // Always "admin" in this case

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please provide valid email and password.", Alert.AlertType.WARNING);
            return;
        }

        // Save to database
        try (Connection connection = DBUtil.getConnection()) {
            // SQL command to insert a new admin user
            String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password); // This example stores passwords in plaintext; hashing is recommended for production.
            stmt.setString(3, role);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Admin account created successfully.", Alert.AlertType.INFORMATION);
                clearInputFields(); // Clear the fields after successful insertion
            } else {
                showAlert("Error", "Failed to create admin account. Please try again.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while saving admin details. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearInputFields() {
        emailField.clear();
        passwordField.clear();
    }
}