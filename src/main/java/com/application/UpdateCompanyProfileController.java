package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateCompanyProfileController {

    @FXML
    private TextField companyNameField;
    @FXML
    private TextField companyWebsiteField;
    @FXML
    private TextField contactPersonField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea companyDescriptionField;

    @FXML
    private Button saveButton;

    private Connection connection;
    private int employerId; // The ID of the authenticated employer

    // Initialize method called by JavaFX runtime
    @FXML
    public void initialize() throws SQLException {
        connection = DBUtil.getConnection();  // Assuming DBUtil contains a valid connection
        employerId = UserSession.getInstance().getUserId(); // Get the authenticated user ID
        loadEmployerDetails();
    }

    private void loadEmployerDetails() {
        String query = "SELECT * FROM employers WHERE employer_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Populate fields with existing employer details
                companyNameField.setText(resultSet.getString("company_name"));
                companyWebsiteField.setText(resultSet.getString("company_website"));
                contactPersonField.setText(resultSet.getString("contact_person"));
                phoneField.setText(resultSet.getString("phone"));
                companyDescriptionField.setText(resultSet.getString("company_description"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Employer details could not be loaded.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading employer details: " + e.getMessage());
        }
    }

    @FXML
    private void saveProfile() {
        // Fetch updated data from fields
        String updatedName = companyNameField.getText();
        String updatedWebsite = companyWebsiteField.getText();
        String updatedContactPerson = contactPersonField.getText();
        String updatedPhone = phoneField.getText();
        String updatedDescription = companyDescriptionField.getText();

        // Validate input
        if (updatedName.isEmpty() || updatedWebsite.isEmpty() || updatedPhone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Company name, website, and phone are required fields.");
            return;
        }

        // Update database with new details
        String updateQuery = "UPDATE employers SET company_name = ?, company_website = ?, contact_person = ?, phone = ?, company_description = ? WHERE employer_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, updatedName);
            preparedStatement.setString(2, updatedWebsite);
            preparedStatement.setString(3, updatedContactPerson);
            preparedStatement.setString(4, updatedPhone);
            preparedStatement.setString(5, updatedDescription);
            preparedStatement.setInt(6, employerId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.ERROR, "Success", "Company profile updated successfully.");
                //clearForm();
                Stage currentStage = (Stage) companyNameField.getScene().getWindow();
                currentStage.close();

                // Navigate back to the Dashboard
                navigateToDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update company profile.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating employer details: " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType error, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void navigateToDashboard() {
        try {
            // Load the Dashboard View FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EmployerDashboardView.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load());

            // Get the primary stage and replace its scene
            Stage primaryStage = (Stage) companyNameField.getScene().getWindow(); // Get the existing stage
            primaryStage.setScene(dashboardScene);
            primaryStage.setTitle("Dashboard");
            primaryStage.show(); // Show the updated dashboard scene
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the dashboard.");
        }
    }


}