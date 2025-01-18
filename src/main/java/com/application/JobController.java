package com.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class JobController {
    @FXML
    public void loadJobs() {
        // Logic to load job listings
        showAlert(Alert.AlertType.INFORMATION, "Jobs Loaded", "Jobs have been successfully loaded.");
    }

    @FXML
    public void handleApply() {
        // Logic to handle job application
        showAlert(Alert.AlertType.INFORMATION, "Application Submitted", "Your application has been submitted.");
    }

    @FXML
    public void handleWithdraw() {
        // Logic to handle application withdrawal
        showAlert(Alert.AlertType.INFORMATION, "Application Withdrawn", "Your application has been withdrawn.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
