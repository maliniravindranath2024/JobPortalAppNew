package com.application;

import com.application.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewJobsForEmployerController {

    @FXML
    private TableView<Job> jobsTable;

    private ObservableList<Job> jobList;
    private Connection connection;
    private int employerId;

    @FXML
    public void initialize() throws SQLException {
        connection = DBUtil.getConnection();  // Get a valid database connection
        employerId = UserSession.getInstance().getUserId(); // Get authenticated user's employer ID

        // Load the jobs posted by the employer
        jobList = FXCollections.observableArrayList();
        fetchJobs();
        jobsTable.setItems(jobList);

        //addEditButtonColumn();
        addDeleteButtonColumn();
    }

    private void fetchJobs() {
        String query = "SELECT * FROM jobs WHERE employer_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create Job objects for each row in the result set
            while (resultSet.next()) {
                Job job = new Job();
                job.setJobId(resultSet.getInt("job_id"));
                job.setTitle(resultSet.getString("title"));
                job.setDescription(resultSet.getString("description"));
                job.setLocation(resultSet.getString("location"));
                job.setSalaryRange(resultSet.getString("salary_range"));
                job.setType(resultSet.getString("type"));
                job.setSkillsRequired(resultSet.getString("skills_required"));
                job.setPostedAt(String.valueOf(resultSet.getTimestamp("posted_at")));
                job.setExpiryDate(String.valueOf(resultSet.getTimestamp("expiry_date")));
                jobList.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while fetching jobs: " + e.getMessage());
        }
    }

//    private void addEditButtonColumn() {
//        TableColumn<Job, Void> editColumn = new TableColumn<>("Edit");
//
//        editColumn.setCellFactory(param -> new TableCell<>() {
//            private final Button editButton = new Button("Edit");
//
//            {
//                editButton.setOnAction(event -> {
//                    Job job = getTableView().getItems().get(getIndex());
//                    if (job != null) {
//                        System.out.println("Edit button clicked for job: " + job.getJobId());
//                        handleEdit(job);
//                    }
//                    else {
//                        System.err.println("No job found at index " + getIndex());
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    setGraphic(editButton);
//                }
//            }
//        });
//
//        jobsTable.getColumns().add(editColumn);
//    }

    // Add a Delete button column to the table
    private void addDeleteButtonColumn() {
        TableColumn<Job, Void> deleteColumn = new TableColumn<>("Delete");

        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setTextFill(Color.RED);
                deleteButton.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    if (job != null) {
                        handleDelete(job);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        jobsTable.getColumns().add(deleteColumn);
    }


    // Callback for "Delete" button action
    public void handleDelete(Job job) {
        String deleteQuery = "DELETE FROM jobs WHERE job_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, job.getJobId());
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                jobList.remove(job); // Remove the job from the list
                showAlert("Success", "Job deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete the job.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the job: " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}