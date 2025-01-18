package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;

public class JobSeekerViewController {

    @FXML
    private StackPane contentPane;

    @FXML
    private void handleViewJobs() {
        loadView("ViewJobs.fxml");
    }

    @FXML
    private void handleApplyForJobs() {
        loadView("ApplyForJobs.fxml");
    }

    @FXML
    private void handleSearchForJobs() {
        loadView("SearchForJobs.fxml");
    }

    @FXML
    private void handleViewStatusOfJob() {
        loadView("ViewJobStatus.fxml");
    }

    @FXML
    private void handleWithdrawApplication() {
        loadView("WithdrawApplication.fxml");
    }

    @FXML
    private void handleEditUpdateProfile() {
        loadView("EditUpdateProfile.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent MainView = fxmlLoader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(MainView));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Unable to load the login view.");
        }
    }

    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}