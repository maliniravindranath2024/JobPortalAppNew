package com.application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditUpdateProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField skillsField;
    @FXML private TextField experienceField;
    @FXML private TextField educationField;
    @FXML private Label resumeLabel;

    private File selectedResumeFile;
    private int userId; // Assume this is passed from the previous screen

    // Initialize data with userId
    public void initializeData(int userId) {
        this.userId = userId;
        // Load existing data from the database and populate the fields
        loadProfileData();
    }

    @FXML
    public void initialize() {
        initializeData(UserSession.getInstance().getUserId());
    }

    private void loadProfileData() {
        String query = "SELECT first_name, last_name, phone, skills, experience_years, education FROM jobseekers WHERE job_seeker_id = ?";

        try (Connection connection = DBConnectionTest.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                firstNameField.setText(resultSet.getString("first_name"));
                lastNameField.setText(resultSet.getString("last_name"));
                phoneField.setText(resultSet.getString("phone"));
                skillsField.setText(resultSet.getString("skills"));
                experienceField.setText(resultSet.getString("experience_years"));
                educationField.setText(resultSet.getString("education"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile data.");
        }
    }

    @FXML
    private void handleUploadResume() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Document Files", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedResumeFile = file;
            resumeLabel.setText(file.getName());
        } else {
            resumeLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String skills = skillsField.getText();
        String education = educationField.getText();
        int experienceYears;

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || skills.isEmpty() || education.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required.");
            return;
        }

        try {
            experienceYears = Integer.parseInt(experienceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Experience must be a valid number.");
            return;
        }

        byte[] resumeContent = readResumeFile(selectedResumeFile);
        if (resumeContent == null) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to read resume.");
            return;
        }

        updateProfileInDatabase(firstName, lastName, phone, resumeContent, skills, experienceYears, education);
    }

    private byte[] readResumeFile(File resumeFile) {
        if (resumeFile == null) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(resumeFile)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to read resume.");
            return null;
        }
    }

    private void updateProfileInDatabase(String firstName, String lastName, String phone, byte[] resumeContent, String skills, int experienceYears, String education) {
        String query = "UPDATE jobseekers SET first_name = ?, last_name = ?, phone = ?, resume = ?, skills = ?, experience_years = ?, education = ? WHERE job_seeker_id = ?";

        try (Connection connection = DBConnectionTest.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, phone);
            statement.setBytes(4, "Resume".getBytes());
            statement.setString(5, skills);
            statement.setInt(6, experienceYears);
            statement.setString(7, education);
            statement.setInt(8, userId);

            System.out.println("Executing query: " + statement);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update profile.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update profile.");
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