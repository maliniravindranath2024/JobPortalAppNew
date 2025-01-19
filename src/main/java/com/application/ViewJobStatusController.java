package com.application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewJobStatusController {

    @FXML
    private TableView<Application> jobStatusTable;

    @FXML
    private TableColumn<Application, Integer> applicationIdColumn;

    @FXML
    private TableColumn<Application, Integer> jobIdColumn;

    @FXML
    private TableColumn<Application, String> statusColumn;

    private int jobSeekerId; // Add this field to store the logged-in job seeker's ID

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
        handleViewJobStatus(); // Call the method to load the data
    }

    @FXML
    public void initialize() {
        setJobSeekerId(UserSession.getInstance().getUserId());
        applicationIdColumn.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void handleViewJobStatus() {
        ObservableList<Application> applications = FXCollections.observableArrayList();
        try {
            // Update the connection string with the correct credentials
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_jobportal", "root", "root");
            Statement statement = connection.createStatement();
            String query = "SELECT application_id, job_id, status FROM applications WHERE job_seeker_id = " + jobSeekerId;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int applicationId = resultSet.getInt("application_id");
                int jobId = resultSet.getInt("job_id");
                String status = resultSet.getString("status");
                applications.add(new Application(applicationId, jobId, jobSeekerId, status));
                System.out.println("Fetched Application: " + applicationId + ", " + jobId + ", " + status); // Debug statement
            }

            jobStatusTable.setItems(applications);
            System.out.println("Total Applications: " + applications.size()); // Debug statement
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}