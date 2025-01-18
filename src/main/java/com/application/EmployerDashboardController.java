package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class EmployerDashboardController {

    @FXML
    private StackPane contentPane; // Center Pane where views will be displayed

    @FXML
    private Label welcomeLabel;

    /**
     * Load the "Add New Job" view into the central content pane.
     */
    @FXML
    public void handleAddNewJob() {
        loadView("AddJobView.fxml");
    }

    /**
     * Load the "View Candidates for a Job" view.
     */
    @FXML
    public void handleViewCandidates() {
        loadView("ViewCandidatesView.fxml");
    }

    /**
     * Load the "View All Jobs Posted" view.
     */
    @FXML
    public void handleViewAllJobs() {
        loadView("ViewAllJobsForEmployer.fxml");
    }

    /**
     * Load the "Delete a Job Posting" view.
     */
    @FXML
    public void handleDeleteJob() {
        loadView("DeleteJobView.fxml");
    }

    /**
     * Load the "View Candidate Profile" view.
     */
    @FXML
    public void handleViewCandidateProfile() {
        loadView("CandidateProfileView.fxml");
    }

    /**
     * Load the "Accept or Reject Candidate" view.
     */
    @FXML
    public void handleAcceptRejectCandidate() {
        loadView("AcceptRejectCandidateView.fxml");
    }

    /**
     * Load the "Update Company Profile" view.
     */
    @FXML
    public void handleUpdateProfile() {
        loadView("UpdateCompanyProfileView.fxml");
    }

    /**
     * Utility method to load an FXML file into the center content pane.
     *
     * @param fxmlFile The FXML file to load.
     */
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