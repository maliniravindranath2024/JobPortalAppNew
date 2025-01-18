package com.application;

import com.application.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class JobController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField salaryField;

    @FXML
    private ComboBox<String> typeComboBox; // Updated to ComboBox

    @FXML
    private TextArea skillsField;

    @FXML
    private DatePicker expiryField;

    private Connection connection;  // Database connection
    private Job job;  // Job being edited

    // Job type options
    private final ObservableList<String> jobTypes = FXCollections.observableArrayList(
            "Full-time", "Part-time", "Remote", "Freelance"
    );

    // Initialize method to set up the ComboBox and database connection
    @FXML
    public void initialize() throws SQLException {
        connection = DBUtil.getConnection(); // Assuming a DBUtil class handles DB connections

        // Populate the ComboBox with job type options
        typeComboBox.setItems(jobTypes);
    }

    /**
     * Populate the form fields with the details of the Job object.
     *
     * @param job The job object being edited.
     */
    public void setJobDetails(Job job) {
        this.job = job;

        if (job == null) {
            System.err.println("Error: Job object passed is null.");
            return;
        }

        System.out.println("Setting job details for job ID: " + job.getJobId());

        // Populate the form fields with Job details
        titleField.setText(job.getTitle());
        descriptionField.setText(job.getDescription());
        locationField.setText(job.getLocation());
        salaryField.setText(job.getSalaryRange());
        typeComboBox.setValue(job.getType()); // Set the selected value of the ComboBox
        skillsField.setText(job.getSkillsRequired());
        if (job.getExpiryDate() != null && !job.getExpiryDate().isEmpty()) {
            expiryField.setValue(LocalDate.parse(job.getExpiryDate())); // Convert String to LocalDate
        }
    }

    /**
     * Save the updated job details to the database.
     */
    @FXML
    private void saveJob() {
        String updatedTitle = titleField.getText();
        String updatedDescription = descriptionField.getText();
        String updatedLocation = locationField.getText();
        String updatedSalaryRange = salaryField.getText();
        String updatedType = typeComboBox.getValue(); // Get selected value from ComboBox
        String updatedSkills = skillsField.getText();
        LocalDate updatedExpiryDate = expiryField.getValue();

        // Validation: Ensure all required fields are completed
        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedLocation.isEmpty() ||
                updatedSalaryRange.isEmpty() || updatedType == null || updatedSkills.isEmpty() ||
                updatedExpiryDate == null) {
            showAlert("Error", "All fields are required to update a job.");
            return;
        }

        // Update the job record in the database
        String updateQuery = "UPDATE jobs SET title = ?, description = ?, location = ?, salary_range = ?, " +
                "type = ?, skills_required = ?, expiry_date = ? WHERE job_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, updatedTitle);
            preparedStatement.setString(2, updatedDescription);
            preparedStatement.setString(3, updatedLocation);
            preparedStatement.setString(4, updatedSalaryRange);
            preparedStatement.setString(5, updatedType); // Save ComboBox selection
            preparedStatement.setString(6, updatedSkills);
            preparedStatement.setDate(7, java.sql.Date.valueOf(updatedExpiryDate)); // Convert LocalDate to SQL Date
            preparedStatement.setInt(8, job.getJobId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Success", "Job updated successfully.");
                closeWindow(); // Close the current window
            } else {
                showAlert("Error", "Failed to update the job. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the job: " + e.getMessage());
        }
    }

    /**
     * Cancel the editing process and close the window.
     */
    @FXML
    private void cancelEdit() {
        closeWindow();
    }

    /**
     * Helper to close the current edit job stage.
     */
    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /**
     * Display an alert dialog with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message content.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}