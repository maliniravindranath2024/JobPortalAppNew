package com.application;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    private void handleAddNewAdmin() {
        loadView("AddAdminView.fxml");
    }

    @FXML
    private void handleViewAllUsers() {
        loadView("ViewAllUsers.fxml");
    }

    @FXML
    private void handleViewAllJobs() {
        loadView("ViewAllJobs.fxml");
    }

     /**
     * Loads a new view into the center contentPane.
     */
    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();

            // Clear the contentPane and add the new view
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the view: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        // Clear session data
        UserSession.getInstance().clearSession();

        try {
            // Load the Login View FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            // Get current stage (window) and set the new scene
            Stage primaryStage = (Stage) contentPane.getScene().getWindow();
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            //showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the Login screen.");
        }
    }


    /**
     * Utility method to display an alert.
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}