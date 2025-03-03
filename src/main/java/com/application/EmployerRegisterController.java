package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployerRegisterController {

    private int user_id; // Store the passed user ID

    // Called to initialize user details
    public void initializeData(int user_id) {
        this.user_id = user_id;
        // Use userId to initialize the profile page (e.g., pre-fill fields if needed)
        System.out.println("Initialized for Employer user ID: " + user_id);
    }

    // FXML Fields
    @FXML
    private TextField companyNameField;
    @FXML private TextField companyWebsiteField;
    @FXML private TextField contactPersonField;
    @FXML private TextField phoneField;
    @FXML private TextArea companyDescriptionField;

    // Handle the Save button click
    @FXML
    public void handleSave() {
        // Collect form data
        String companyName = companyNameField.getText();
        String companyWebsite = companyWebsiteField.getText();
        String contactPerson = contactPersonField.getText();
        String phone = phoneField.getText();
        String companyDescription = companyDescriptionField.getText();

        // Validate input
        if (companyName.isEmpty() || companyWebsite.isEmpty() || contactPerson.isEmpty() || phone.isEmpty() || companyDescription.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        // Save data to the database
        String query = "INSERT INTO employers (employer_id, company_name, company_website, contact_person, phone, company_description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionTest.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters in the query
            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, companyName);
            preparedStatement.setString(3, companyWebsite);
            preparedStatement.setString(4, contactPerson);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, companyDescription);


            // Execute the statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Company details saved successfully!");
                handleNavigateToEmployerView();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving company details.");
        }
    }

    private void handleNavigateToEmployerView() {
        try {
            // Load EmployerDashboardView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployerDashboardView.fxml"));
            Parent root = loader.load();
// Debug check for controller and scene
            System.out.println("EmployerDashboardView.fxml loaded successfully.");
            // Pass data to EmployerViewController if needed
            EmployerDashboardController controller = loader.getController();
            // Example: Passing employer data (optional)
            // controller.initializeEmployerDetails(companyName, employerId);

            // Get the current stage and set new scene
            Stage stage = (Stage) companyNameField.getScene().getWindow(); // Access current stage
            stage.setScene(new Scene(root)); // Set EmployerDashboardView.fxml scene
            stage.setTitle("Employer Dashboard"); // Set a new title for the view
            stage.show();

            System.out.println("Redirected to Employer Dashboard.");


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load EmployerView.");
        }
    }

    // Handle the Cancel button click
    @FXML
    public void handleCancel() {
        // Clear all text fields and text areas in the form
        companyNameField.clear();
        companyWebsiteField.clear();
        contactPersonField.clear();
        phoneField.clear();
        companyDescriptionField.clear();

        System.out.println("Form fields cleared.");
    }



    // Utility method for alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
