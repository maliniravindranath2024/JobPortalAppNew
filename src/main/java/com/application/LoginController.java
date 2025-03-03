package com.application;

import com.application.DBUtil;
import com.application.UserSession;

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
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() throws SQLException
    {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter both username and password.");
            return;
        }
    // String role = authenticateUser(username, password);
        ResultSet userDetails = authenticateUser(email, password);
        if (userDetails == null) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
        else {
            try {
                int userId = userDetails.getInt("user_id");
                String role = userDetails.getString("role");

                UserSession.getInstance().setUserId(userId);
                UserSession.getInstance().setRole(role);
                loadViewForRole(role);
            }
            catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Loading Error", "Unable to load the view for your role.");
                e.printStackTrace();
            }
        }
    }

    private ResultSet authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT user_id, role FROM users WHERE email = ? AND password = ?";

        try {
            Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Return the user's role
                return resultSet;
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
            fxmlFile = "AdminDashboardView.fxml";
            break;
        case "recruiter":
            fxmlFile = "EmployerDashboardView.fxml";
            break;
        case "job_seeker":
            fxmlFile = "JobSeekerDashboardView.fxml";
            break;
        default:
            showAlert(Alert.AlertType.ERROR, "Role Error", "Unknown role: " + role);
            return;
    }

    // Load the FXML and display it
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
    Scene scene = new Scene(fxmlLoader.load());

    // Use the existing stage to display the new scene
    Stage stage = (Stage) emailField.getScene().getWindow();
    stage.setScene(scene);
    stage.sizeToScene();
    stage.setMinWidth(1000);
    stage.setMinHeight(800);
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
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(registerScene);
            stage.sizeToScene();
            stage.setMinWidth(1000);
            stage.setMinHeight(800);
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
