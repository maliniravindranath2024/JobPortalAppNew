package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateCandidateStatusController {

    @FXML
    private ComboBox<String> jobDropDown;

    @FXML
    private TableView<Application> applicationTable;

    @FXML
    private TableColumn<Application, Integer> applicationIdCol;

    @FXML
    private TableColumn<Application, Integer> jobIdCol;

    @FXML
    private TableColumn<Application, Integer> jobSeekerIdCol;

    @FXML
    private TableColumn<Application, String> statusCol;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private ObservableList<Application> applications;

    @FXML
    public void initialize() {
        // Initialize table columns
        applicationIdCol.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        jobIdCol.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        jobSeekerIdCol.setCellValueFactory(new PropertyValueFactory<>("jobSeekerId"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate dropdown with jobs
        populateJobDropDown();

        // Add listener to fetch applications when a job is selected
        jobDropDown.setOnAction(this::onJobSelected);

        // Add click listeners for Accept and Reject buttons
        acceptButton.setOnAction(this::onAccept);
        rejectButton.setOnAction(this::onReject);
    }

    private void populateJobDropDown() {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT job_id FROM jobs WHERE employer_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, getAuthenticatedUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobDropDown.getItems().add(rs.getString("job_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onJobSelected(ActionEvent event) {
        int selectedJob = Integer.parseInt(jobDropDown.getSelectionModel().getSelectedItem());
        if (!jobDropDown.getSelectionModel().isEmpty() && selectedJob != 0) {
            fetchApplicationsForJob(selectedJob);
        }
    }

    private void fetchApplicationsForJob(int job_id) {
        applications = FXCollections.observableArrayList();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = """
                    SELECT a.application_id, a.job_id, a.job_seeker_id, a.status
                    FROM applications a
                    WHERE a.status = 'under review'
                    """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getInt("job_id"),
                        rs.getInt("job_seeker_id"),
                        rs.getString("status")
                ));
            }

            applicationTable.setItems(applications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onAccept(ActionEvent event) {
        Application selectedApplication = applicationTable.getSelectionModel().getSelectedItem();
        if (selectedApplication != null) {
            updateApplicationStatus(selectedApplication, "accepted");
        } else {
            showAlert("No Selection", "Please select a candidate to accept.");
        }
    }

    private void onReject(ActionEvent event) {
        Application selectedApplication = applicationTable.getSelectionModel().getSelectedItem();
        if (selectedApplication != null) {
            updateApplicationStatus(selectedApplication, "rejected");
        } else {
            showAlert("No Selection", "Please select a candidate to reject.");
        }
    }

    private void updateApplicationStatus(Application application, String newStatus) {
        try (Connection connection = DBUtil.getConnection()) {

            int applicationId = application.getApplicationId();
            // Update application status and interview status
            String sql = """
                    UPDATE applications
                    SET status = ? WHERE application_id = ?
                    """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, applicationId);
            stmt.executeUpdate();

            String updateInterviewsql = "Update interviews set status = 'completed' where application_id = ?";
            PreparedStatement updateInterviewStmt = connection.prepareStatement(updateInterviewsql);
            updateInterviewStmt.setInt(1, applicationId);
            updateInterviewStmt.executeUpdate();

            // Refresh the table after updating
            int selectedJob = Integer.parseInt(jobDropDown.getSelectionModel().getSelectedItem());
            if (selectedJob != 0 && !jobDropDown.getSelectionModel().isEmpty()) {
                fetchApplicationsForJob(selectedJob);
            }

            showAlert("Success", "Candidate status updated to " + newStatus + ".");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getAuthenticatedUserId() {
        UserSession session = UserSession.getInstance();
        String role = session.getRole();
        int userId = session.getUserId();
        return userId;
    }
}