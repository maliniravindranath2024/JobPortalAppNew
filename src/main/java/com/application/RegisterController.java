package com.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    public RadioButton jobseekerRadioButton;
    public RadioButton employerRadioButton;
    // FXML fields corresponding to RegisterView.fxml
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleGroup roleToggleGroup;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and assign RadioButtons to it
        roleToggleGroup = new ToggleGroup();
        jobseekerRadioButton.setToggleGroup(roleToggleGroup);
        employerRadioButton.setToggleGroup(roleToggleGroup);

        // Optionally, set a default selection
        jobseekerRadioButton.setSelected(true); // Jobseeker is selected by default
    }

    // Method to handle login
    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all required fields.");
            return;
        }

        // Check credentials in the database
        String query = "SELECT user_id, role FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DBConnectionTest.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Login successful, redirect to the appropriate profile page
                    String role = resultSet.getString("role");
                    int userId = resultSet.getInt("user_id");

                    UserSession.getInstance().setUserId(userId);
                    UserSession.getInstance().setRole(role);

                    if (role.equalsIgnoreCase("jobseeker")) {
                        openPage("JobseekerProfileView.fxml", userId);
                    } else if (role.equalsIgnoreCase("employer")) {
                        openPage("EmployerProfileView.fxml", userId);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid email or password.");
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while checking user credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to handle registration
    @FXML
    public void handleRegister() {
        // Collect data from fields
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = "";

        if (roleToggleGroup.getSelectedToggle() == jobseekerRadioButton) {
            role = "job_seeker";
        } else if (roleToggleGroup.getSelectedToggle() == employerRadioButton) {
            role = "recruiter";
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a role.");
            return;
        }

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all required fields.");
            return;
        }

        // Save to database
        String query = "INSERT INTO users (role, email, password) " +
                "VALUES (?, ?, ?)";

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID()";

        try (Connection connection = DBConnectionTest.getConnection(); // Assuming a DBUtil class for DB connection
             PreparedStatement insertStatement = connection.prepareStatement(query);
             PreparedStatement lastIdStatement = connection.prepareStatement(lastInsertIdQuery)){

            insertStatement.setString(1, role);
            insertStatement.setString(2, email);
            insertStatement.setString(3, password);

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted == 0) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "User registration failed.");
                return;
            }

            // Retrieve the last inserted user ID
            int lastInsertedId = 0;
            try (ResultSet resultSet = lastIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    lastInsertedId = resultSet.getInt(1); // Get the ID
                }
            }

            if (lastInsertedId == 0) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve the user ID.");
                return;
            }

            // Redirect to the appropriate profile creation page based on role
            if (role.equalsIgnoreCase("job_seeker")) {
                openPage("JobseekerProfileView.fxml", lastInsertedId);
            } else if (role.equalsIgnoreCase("recruiter")) {
                openPage("EmployerProfileView.fxml", lastInsertedId);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while saving user details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openPage(String fxmlFile, int user_id) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/application/" + fxmlFile));
            Parent root = loader.load();

            // Pass the user ID to the next controller
            if (fxmlFile.equals("JobseekerProfileView.fxml")) {
                JobSeekerController controller = loader.getController();
                controller.initializeData(user_id); // Pass user ID
            } else if (fxmlFile.equals("EmployerProfileView.fxml")) {
                EmployerController controller = loader.getController();
                controller.initializeData(user_id); // Pass user ID
            }

            // Open the new page in the current stage
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the next page.");
        }
    }

    @FXML
    public void handleCancel() {
        // Ask for confirmation before closing
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                System.exit(0);
            }
        });
    }

    // Utility method to display alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}