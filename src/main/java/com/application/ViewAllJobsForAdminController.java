package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAllJobsForAdminController {

        @FXML
        private TableView<Job> jobsTableView;

        @FXML
        private TableColumn<Job, Integer> jobIdColumn;

        @FXML
        private TableColumn<Job, String> jobTitleColumn;

        @FXML
        private TableColumn<Job, String> jobDescriptionColumn;

        @FXML
        private TableColumn<Job, Void> actionColumn;

        private ObservableList<Job> jobsList = FXCollections.observableArrayList();

        @FXML
        public void initialize() {
            // Bind table columns to Job properties
            jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
            jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            jobDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            //jobPostedByColumn.setCellValueFactory(new PropertyValueFactory<>("postedBy"));

            // Add a Delete button to the action column
            initializeDeleteButton();

            // Load job postings from the database
            loadJobs();
        }

        /**
         * Load all job postings from the database and set them in the TableView.
         */
        private void loadJobs() {
            try (Connection connection = DBUtil.getConnection()) {
                String sql = "SELECT job_id, title, description FROM jobs" ;

                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                // Clear the existing jobsList
                jobsList.clear();

                while (rs.next()) {
                    int jobId = rs.getInt("job_id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    //String postedBy = rs.getString("posted_by");

                    jobsList.add(new Job(jobId, title, description));
                }

                // Populate the TableView with fetched job data
                jobsTableView.setItems(jobsList);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load job postings. Please try again later.", Alert.AlertType.ERROR);
            }
        }

        /**
         * Add a Delete button to each row in the 'actionColumn', bound to delete the selected job.
         */
        private void initializeDeleteButton() {
            // Custom Cell Factory for action column to include the delete button for each row
            Callback<TableColumn<Job, Void>, TableCell<Job, Void>> cellFactory = param -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        Job selectedJob = getTableView().getItems().get(getIndex());
                        if (selectedJob != null) {
                            deleteJob(selectedJob);
                        } else {
                            showAlert("Error", "No job selected for deletion.", Alert.AlertType.ERROR);
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
            };

            actionColumn.setCellFactory(cellFactory);
        }

        /**
         * Delete the selected job posting along with its related entries in the 'applications' table.
         */
        private void deleteJob(Job job) {
            try (Connection connection = DBUtil.getConnection()) {

                // Delete dependent records in the "applications" table
                String deleteApplicationsSql = "DELETE FROM applications WHERE job_id = ?";
                try (PreparedStatement deleteApplicationsStmt = connection.prepareStatement(deleteApplicationsSql)) {
                    deleteApplicationsStmt.setInt(1, job.getJobId());
                    deleteApplicationsStmt.executeUpdate();
                }

                // Delete the job from the "jobs" table
                String deleteJobSql = "DELETE FROM jobs WHERE job_id = ?";
                try (PreparedStatement deleteJobStmt = connection.prepareStatement(deleteJobSql)) {
                    deleteJobStmt.setInt(1, job.getJobId());

                    int rowsAffected = deleteJobStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        jobsList.remove(job);
                        jobsTableView.refresh();
                        showAlert("Success", "Job posting deleted successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to delete the job posting. Please try again.", Alert.AlertType.ERROR);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the job posting.", Alert.AlertType.ERROR);
            }
        }

        /**
         * Utility method to display an alert dialog.
         */
        private void showAlert(String title, String message, Alert.AlertType alertType) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

