package com.application;

import com.application.DBUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter both username and password.");
            return;
        }
String role = authenticateUser(username, password);
        if (role == null) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        } else {
            try {
                loadViewForRole(role);
            }
            catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Loading Error", "Unable to load the view for your role.");
                e.printStackTrace();
            }
        }
            }

    private String authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT role FROM users WHERE email = ? AND password = ?";

        try {
            Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Return the user's role
                return resultSet.getString("role");
            }
        }
        catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot connect to the database. Please try again later.");
        e.printStackTrace();
    }
        // Return null if authentication fails
        return null;
}
private void loadViewForRole(String role) throws IOException {
    String fxmlFile;

    // Select the appropriate FXML file
    switch (role.toLowerCase()) {
        case "admin":
            fxmlFile = "AdminView.fxml";
            break;
        case "recruiter":
            fxmlFile = "RecruiterView.fxml";
            break;
        case "job_seeker":
            fxmlFile = "JobSeekerView.fxml";
            break;
        default:
            showAlert(Alert.AlertType.ERROR, "Role Error", "Unknown role: " + role);
            return;
    }

    // Load the FXML and display it
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
    Scene scene = new Scene(fxmlLoader.load());

    // Use the existing stage to display the new scene
    Stage stage = (Stage) usernameField.getScene().getWindow();
    stage.setScene(scene);
    stage.setTitle(role + " Dashboard");
    stage.show();
}

    @FXML
    private void handleRegister() {
        try {
            // Load the registration view (RegisterView.fxml)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));
            Scene registerScene = new Scene(fxmlLoader.load());

            // Use the existing stage to display the registration view
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("Register");
            stage.show();
        }
        catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Unable to load the registration view.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
