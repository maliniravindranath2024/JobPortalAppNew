package com.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class FeedbackController {
    @FXML
    public void handleFeedback() {
        // Logic to submit feedback
        showAlert(Alert.AlertType.INFORMATION, "Feedback Sent", "Your feedback has been submitted. Thank you!");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
