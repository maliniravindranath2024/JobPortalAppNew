package com.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class ApplicationStatusController {
    @FXML
    public void handleViewStatus() {
        // Logic to display application status
        showAlert(Alert.AlertType.INFORMATION, "Status Viewed", "Your application status has been loaded.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
