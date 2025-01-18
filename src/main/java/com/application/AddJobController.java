package com.application;

import com.application.DBConnectionTest;
import com.application.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddJobController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField salaryRangeField;

    @FXML
    private ComboBox<String> typeBox;

    @FXML
    private TextArea skillsField;

    @FXML
    private TextField postedAtField;

    @FXML
    private TextField expiryDateField;

    /**
     * Handle the submission of the form to save the job data.
     */
    @FXML
    public void handleSubmit() {
        // Get input data
        String title = titleField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String salaryRange = salaryRangeField.getText();
        String type = typeBox.getValue();
        String skillsRequired = skillsField.getText();
        String postedAt = postedAtField.getText();
        String expiryDate = expiryDateField.getText();

        UserSession session = UserSession.getInstance();
        String role = session.getRole();
        int userId = session.getUserId();
// System.out.println("Role: " + role);
// System.out.println("User ID: " + userId);
        // Input Validation
        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type == null || skillsRequired.isEmpty() || postedAt.isEmpty() || expiryDate.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        if (!isEmployerIdValid(userId)) {
            System.out.println("Employer ID does not exist!");
        }
        else {
            // Save data to database
            String query = "INSERT INTO jobs (employer_id, title, description, location, salary_range, type, skills_required, posted_at, expiry_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DBConnectionTest.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                // Hardcoded employer_id for demonstration, replace with authenticated employer's ID
                //int employerId = 1;

                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, description);
                preparedStatement.setString(4, location);
                preparedStatement.setString(5, salaryRange);
                preparedStatement.setString(6, type);
                preparedStatement.setString(7, skillsRequired);
                preparedStatement.setDate(8, java.sql.Date.valueOf(postedAt));
                preparedStatement.setDate(9, java.sql.Date.valueOf(expiryDate));

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Job successfully added!");
                    clearForm();
                    Stage currentStage = (Stage) titleField.getScene().getWindow();
                    currentStage.close();

                    // Navigate back to the Dashboard
                    navigateToDashboard();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save the job. Please try again.");
            }
        }
    }

    public void navigateToDashboard() {
        try {
            // Load the Dashboard View FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EmployerDashboardView.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load());

            // Get the primary stage and replace its scene
            Stage primaryStage = (Stage) titleField.getScene().getWindow(); // Get the existing stage
            primaryStage.setScene(dashboardScene);
            primaryStage.setTitle("Dashboard");
            primaryStage.show(); // Show the updated dashboard scene
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the dashboard.");
        }
    }
    /**
     * Handle cancel button to clear the form.
     */
    @FXML
    public void handleCancel() {
        clearForm();
        Stage currentStage = (Stage) titleField.getScene().getWindow();
        currentStage.close();

    }

    private boolean isEmployerIdValid(int employerId)
    {
        String query = "SELECT * FROM employers WHERE employer_id = ?";

        try (Connection connection = DBConnectionTest.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, employerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println(resultSet.toString());
            // Return true if there is at least one row
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Clear all input fields.
     */
    private void clearForm()
    {
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        salaryRangeField.clear();
        typeBox.setValue(null);
        skillsField.clear();
        postedAtField.clear();
        expiryDateField.clear();
    }

    /**
     * Utility method to show alert messages.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}