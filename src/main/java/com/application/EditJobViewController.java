package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditJobViewController {

    @FXML
    private ComboBox<Job> jobDropdown;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField skillsField;

    @FXML
    private TextField expiryDateField;

    @FXML
    private Button saveButton;

    private Connection connection;
    private ObservableList<Job> jobList;

    @FXML
    public void initialize() throws SQLException {
        // Initialize database connection
        connection = DBUtil.getConnection();

        // Load jobs for the authenticated user and populate the ComboBox
        loadJobsForUser();

        // Add listener for job selection
        jobDropdown.getSelectionModel().selectedItemProperty().addListener((obs, oldJob, selectedJob) -> {
            if (selectedJob != null) {
                displayJobDetails(selectedJob); // Populate job details in text fields
            }
        });
    }

    // Load jobs for the logged-in employer
    private void loadJobsForUser() throws SQLException {
        jobList = FXCollections.observableArrayList();
        int employerId = UserSession.getInstance().getUserId(); // Get authenticated user's employer ID

        String query = "SELECT * FROM jobs WHERE employer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Job job = new Job();
                job.setJobId(resultSet.getInt("job_id"));
                job.setTitle(resultSet.getString("title"));
                job.setDescription(resultSet.getString("description"));
                job.setLocation(resultSet.getString("location"));
                job.setSalaryRange(resultSet.getString("salary_range"));
                job.setType(resultSet.getString("type"));
                job.setSkillsRequired(resultSet.getString("skills_required"));
                job.setExpiryDate(resultSet.getString("expiry_date"));

                jobList.add(job);
            }
        }

        // Populate the ComboBox
        jobDropdown.setItems(jobList);
    }

    // Display job details in the input fields
    private void displayJobDetails(Job job) {
        titleField.setText(job.getTitle());
        descriptionField.setText(job.getDescription());
        locationField.setText(job.getLocation());
        salaryField.setText(job.getSalaryRange());
        typeField.setText(job.getType());
        skillsField.setText(job.getSkillsRequired());
        expiryDateField.setText(job.getExpiryDate());
    }

    // Handle Save Changes button click
    @FXML
    private void handleSaveChanges() {
        Job selectedJob = jobDropdown.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            showAlert("Error", "No job selected. Please select a job to edit.");
            return;
        }

        try {
            // Update job details in the database
            String updateQuery = "UPDATE jobs SET title = ?, description = ?, location = ?, salary_range = ?, type = ?, skills_required = ?, expiry_date = ? WHERE job_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, titleField.getText());
                preparedStatement.setString(2, descriptionField.getText());
                preparedStatement.setString(3, locationField.getText());
                preparedStatement.setString(4, salaryField.getText());
                preparedStatement.setString(5, typeField.getText());
                preparedStatement.setString(6, skillsField.getText());
                preparedStatement.setString(7, expiryDateField.getText());
                preparedStatement.setInt(8, selectedJob.getJobId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert("Success", "Job details updated successfully.");
                    loadJobsForUser(); // Reload the jobs after update
                } else {
                    showAlert("Error", "Failed to update job details. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving changes: " + e.getMessage());
        }
    }

    // Helper method to display alert dialogs
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}