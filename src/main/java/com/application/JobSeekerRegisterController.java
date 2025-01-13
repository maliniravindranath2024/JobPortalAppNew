package com.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JobSeekerRegisterController {
    private int userId; // Store the passed user ID
    private File selectedResumeFile; // Stores the uploaded file
    // FXML Fields
    @FXML
    private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField skillsField;
    @FXML private TextField experienceField;
    @FXML private TextField educationField;
    @FXML private Label resumeLabel;

    // Initialize data with userId
    public void initializeData(int userId) {
        this.userId = userId;
        //System.out.println("Jobseeker Profile initialized with User ID: " + userId);
    }

    // Handle resume upload
    @FXML
    public void handleUploadResume() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Document Files", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
// Open the FileChooser dialog
        File file = fileChooser.showOpenDialog(new Stage()); // Correct local variable

        // Assign the selected file to the class-level variable
        if (file != null) {
            selectedResumeFile = file;
            // Properly assign to class-level variable
            resumeLabel.setText(file.getName()); // Display the selected file's name
        } else {
            resumeLabel.setText("No file selected");
        }
    }

    // Handle form submission and save data to the database
    @FXML
    public void handleSave() {
        // Collect form data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String skills = skillsField.getText();
        String education = educationField.getText();
        int experienceYears;

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || skills.isEmpty() || education.isEmpty() || selectedResumeFile == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required, including resume upload.");
            return;
        }

        try {
            experienceYears = Integer.parseInt(experienceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Experience must be a valid number.");
            return;
        }

        // Save the uploaded resume to a predefined location (you can adjust the storage strategy as needed)
        String resumePath = saveResumeFile(selectedResumeFile);
        if (resumePath == null) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save resume.");
            return;
        }
        // SQL Query to insert data into `jobseekers` table
        String query = "INSERT INTO jobseekers (job_seeker_id, first_name, last_name, phone, resume, skills, experience_years, education) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection(); // Assuming DBUtil handles database connections
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values into the query
            statement.setInt(1, userId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, phone);
            statement.setString(5, resumePath); // Resume file path
            statement.setString(6, skills);
            statement.setInt(7, experienceYears);
            statement.setString(8, education);

            // Execute the query
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile saved successfully!");
                handleNavigateToJobSeekerView();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save profile.");
        }
    }
    private void handleNavigateToJobSeekerView() {
        try {
            // Load JobSeekerDashboardView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JobSeekerDashboardView.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) firstNameField.getScene().getWindow(); // Use any node from the current scene
            stage.setScene(new Scene(root)); // Set the new scene
            stage.setTitle("Job Seeker Dashboard"); // Update the window title
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load JobSeekerView.");
        }
    }
    // Cancel button logic
    @FXML
    public void handleCancel() {
        System.out.println("Cancelled. Closing the form.");
        System.exit(0); // Adjust as necessary to return to the previous screen
    }

    // Save the selected resume file to a predefined location
    private String saveResumeFile(File resumeFile) {
        String destDirectory = "uploads/resumes/";
        File targetDir = new File(destDirectory);
        File destFile = new File(destDirectory + resumeFile.getName());
// Ensure the directory exists
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to create directory for resumes.");
            return null;
        }
        try {
            Files.copy(resumeFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save resume.");
            return null;
        }
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
