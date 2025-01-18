package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class InterviewController {

    @FXML
    private ComboBox<String> jobComboBox;

    @FXML
    private TableView<Application> applicantsTable;

    @FXML
    private TableColumn<Application, Integer> applicationIdColumn;

    @FXML
    private TableColumn<Application, Integer> jobSeekerIdColumn;

    @FXML
    private TableColumn<Application, String> statusColumn;

    @FXML
    private DatePicker scheduledDatePicker;

    @FXML
    private ComboBox<String> timePicker;

    @FXML
    private ComboBox<String> interviewTypeCombo;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TextArea feedbackField;

    @FXML
    private Button saveButton;

    private int employerId; // Mock employer ID, replace with authenticated user ID

    public void initialize() {

        UserSession session = UserSession.getInstance();
        String role = session.getRole();
        employerId = session.getUserId();

        // Initialize ComboBox and TableView Columns
        loadJobsForEmployer();
        applicationIdColumn.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        jobSeekerIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobSeekerId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add listener for job selection
        jobComboBox.valueProperty().addListener((obs, oldJob, newJob) -> loadApplicantsForJob(newJob));
    }

    private void loadJobsForEmployer() {
        ObservableList<String> jobs = FXCollections.observableArrayList();
        try (Connection connection = DBUtil.getConnection()) {
            String query = "SELECT job_id FROM jobs WHERE employer_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, employerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                jobs.add(String.valueOf(rs.getInt("job_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jobComboBox.setItems(jobs);
    }

    private void loadApplicantsForJob(String jobId) {
        ObservableList<Application> applicants = FXCollections.observableArrayList();
        try (Connection connection = DBUtil.getConnection()) {
            String query = "SELECT * FROM applications WHERE job_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(jobId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int applicationId = rs.getInt("application_id");
                int jobSeekerId = rs.getInt("job_seeker_id");
                String status = rs.getString("status");
                applicants.add(new Application(applicationId, Integer.parseInt(jobId), jobSeekerId, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        applicantsTable.setItems(applicants);
    }

    @FXML
    private void handleSaveInterview() throws SQLException {
        Application selectedApplication = applicantsTable.getSelectionModel().getSelectedItem();
        if (selectedApplication == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select an applicant.");
            return;
        }
        String formattedScheduledDateTime = null;
        // Get interview details
        LocalDate scheduledDate = scheduledDatePicker.getValue();
        String scheduledTime = timePicker.getValue();
        String interviewType = interviewTypeCombo.getValue().toLowerCase();
        String status = statusCombo.getValue();
        String feedback = feedbackField.getText();

        if (scheduledTime == null || scheduledTime.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Time", "Please select a valid time.");
            return;
        }

        System.out.println("Scheduled Time: '" + scheduledTime + "'");
        System.out.println("Scheduled Time (length): " + (scheduledTime == null ? "null" : scheduledTime.length()));
        scheduledTime = scheduledTime.replace("\u00A0", " ").trim();

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
            LocalTime localTime = LocalTime.parse(scheduledTime.trim(), timeFormatter);
            LocalDateTime scheduledDateTime = LocalDateTime.of(scheduledDate, localTime);
            DateTimeFormatter mysqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formattedScheduledDateTime = scheduledDateTime.format(mysqlFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace(); // Log stack trace to help debug
            showAlert(Alert.AlertType.ERROR, "Invalid Time", "Please select a valid time (e.g., 10:00 AM).");
            return;
        }

        // Save interview to database
        try (Connection connection = DBUtil.getConnection()) {
            String insertQuery = "INSERT INTO interviews (application_id, employer_id, job_seeker_id, scheduled_date, interview_type, status, feedback) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, selectedApplication.getApplicationId());
            stmt.setInt(2, employerId);
            stmt.setInt(3, selectedApplication.getJobSeekerId());
            stmt.setString(4, formattedScheduledDateTime);
            stmt.setString(5, interviewType);
            stmt.setString(6, status);
            stmt.setString(7, feedback);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                String updateApplicationQuery = "UPDATE applications SET status = \'under review\' WHERE application_id = ?";
                PreparedStatement updateApplicationStmt = connection.prepareStatement(updateApplicationQuery);
                //updateApplicationStmt.setString(1, status);
                updateApplicationStmt.setInt(1, selectedApplication.getApplicationId());
                updateApplicationStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Interview scheduled successfully.");
                returnToDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to schedule interview.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }
    private void returnToDashboard() {
        try {
            // Get the current stage (window)
            Stage currentStage = (Stage) saveButton.getScene().getWindow();

            // Load the dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/application/EmployerDashboardView.fxml"));
            Parent dashboardRoot = loader.load();

            // Create a new scene for the dashboard
            Scene dashboardScene = new Scene(dashboardRoot);

            // Set the dashboard scene to the current stage
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("Employer Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard view.");
        }
    }
private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}
}