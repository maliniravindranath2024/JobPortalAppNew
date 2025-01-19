package com.application;

import com.application.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApplyForJobsController {

    @FXML
    private TableView<Job> jobTable;
    @FXML
    private TableColumn<Job, Integer> jobIdColumn;
    @FXML
    private TableColumn<Job, String> titleColumn;
    @FXML
    private TableColumn<Job, String> descriptionColumn;
    @FXML
    private TableColumn<Job, String> locationColumn;
    @FXML
    private TableColumn<Job, Void> applyColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField skillsField;
    @FXML
    private TextField experienceField;

    private File coverLetterFile;
    private File cvFile;

    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private int selectedJobId;
    private int jobSeekerId = 1; // Replace with actual job seeker ID

    @FXML
    public void initialize() {
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("job_id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        applyColumn.setCellFactory(param -> new TableCell<>() {
            private final Button applyButton = new Button("Apply");

            {
                applyButton.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    handleApply(job);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(applyButton);
                }
            }
        });

        loadJobsFromDatabase();
        jobTable.setItems(jobList);
    }

    private void loadJobsFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/db_jobportal";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM jobs")) {

            while (resultSet.next()) {
                int jobId = resultSet.getInt("job_id");
                int employerId = resultSet.getInt("employer_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String salaryRange = resultSet.getString("salary_range");
                String type = resultSet.getString("type");
                String skillsRequired = resultSet.getString("skills_required");
                String postedAt = resultSet.getString("posted_at");
                String expiryDate = resultSet.getString("expiry_date");

                Job job = new Job(jobId, employerId, title, description, location, salaryRange, type, skillsRequired, postedAt, expiryDate);
                jobList.add(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleApply(Job job) {
        selectedJobId = job.getJobId();
        positionField.setText(job.getTitle());
        locationField.setText(job.getLocation());
    }

    @FXML
    private void handleUploadCoverLetter() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Cover Letter");
        coverLetterFile = fileChooser.showOpenDialog(null);
    }

    @FXML
    private void handleUploadCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload CV");
        cvFile = fileChooser.showOpenDialog(null);
    }

    @FXML
    private void handleSubmitApplication() {
        String name = nameField.getText();
        String position = positionField.getText();
        String location = locationField.getText();
        String skills = skillsField.getText();
        String experience = experienceField.getText();

        if (name.isEmpty() || position.isEmpty() || location.isEmpty() || skills.isEmpty() || experience.isEmpty() || coverLetterFile == null || cvFile == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields and upload both cover letter and CV.");
            return;
        }

        saveApplicationToDatabase(selectedJobId, jobSeekerId, name, position, location, skills, experience, coverLetterFile, cvFile, "Pending");

        showAlert(Alert.AlertType.INFORMATION, "Application Submitted", "Your application has been submitted successfully.");
    }

    private void saveApplicationToDatabase(int jobId, int jobSeekerId, String name, String position, String location, String skills, String experience, File coverLetterFile, File cvFile, String status) {
        String url = "jdbc:mysql://localhost:3306/db_jobportal";
        String user = "root";
        String password = "root";

        String insertSQL = "INSERT INTO applications (job_id, job_seeker_id, name, position_applied_for, location, skills, experience, cover_letter, cv, status, date_of_application) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, jobId);
            preparedStatement.setInt(2, jobSeekerId);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, position);
            preparedStatement.setString(5, location);
            preparedStatement.setString(6, skills);
            preparedStatement.setString(7, experience);

            try (InputStream coverLetterStream = new FileInputStream(coverLetterFile);
                 InputStream cvStream = new FileInputStream(cvFile)) {
                preparedStatement.setBlob(8, coverLetterStream);
                preparedStatement.setBlob(9, cvStream);
            }

            preparedStatement.setString(10, status);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debugging statement
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}