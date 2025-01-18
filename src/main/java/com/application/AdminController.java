package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private StackPane contentPane;
//    private void loadContent(String fxmlFile) {
//        loadView(fxmlFile);
//    }

    @FXML
    private Button addNewAdminButton;

    @FXML
    private Button viewAllUsersButton;

    @FXML
    private Button viewAllJobsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void handleAddNewAdmin() {
        // Load the Add New Admin content into the contentArea
        loadView("AddAdminView.fxml");
    }

    @FXML
    private void handleViewAllUsers() {
        // Load the View All Users content into the contentArea
        loadView("ViewAllUsers.fxml");
    }

    @FXML
    private void handleViewAllJobs() {
        // Load the View All Jobs content into the contentArea
        loadView("ViewAllJobs.fxml");
    }
    private void loadView(String fxmlFile) {
        try {
            // Load the new view
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Clear the existing content and set new content
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the requested view: " + fxmlFile);
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
     * Utility method to show alerts.
     *
     * @param error
     * @param title   The title of the alert.
     * @param message The alert message.
     */
    private void showAlert(Alert.AlertType error, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}